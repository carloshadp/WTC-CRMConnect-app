package com.wtc.crmconnect.app.data.remote

import com.wtc.crmconnect.app.data.local.TokenStorage
import com.wtc.crmconnect.app.data.remote.api.AuthApi
import com.wtc.crmconnect.app.data.remote.dto.auth.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Quando o backend responde 401, tenta refresh em /auth/refresh.
 * Em sucesso, persiste novos tokens e re-executa a request original.
 * Em falha, limpa storage (forçando re-login) e devolve `null` (deixa cair 401).
 *
 * Usa `Provider<AuthApi>` para quebrar o ciclo: AuthApi depende do Retrofit que depende
 * do OkHttp que depende deste Authenticator.
 *
 * Mutex sincroniza refresh paralelo: se múltiplas requests recebem 401 simultaneamente,
 * apenas a primeira faz refresh; as outras esperam e reutilizam o novo token.
 */
@Singleton
class AuthAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApiProvider: Provider<AuthApi>
) : Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evita loop infinito: se a request já foi retentada uma vez, desistir.
        if (responseCount(response) >= 2) return null

        // Evita retry em chamadas para /auth/refresh (gera loop)
        if (response.request.url.encodedPath.endsWith("/auth/refresh")) return null

        val currentRefreshToken = tokenStorage.getRefreshToken() ?: return null

        return runBlocking {
            refreshMutex.withLock {
                // Outra thread pode ter feito refresh enquanto esperávamos a lock.
                val latestAccess = tokenStorage.getAccessToken()
                val originalAccess = response.request.header("Authorization")?.removePrefix("Bearer ")
                if (latestAccess != null && latestAccess != originalAccess) {
                    return@withLock buildRetryRequest(response.request, latestAccess)
                }

                try {
                    val refreshed = authApiProvider.get().refresh(RefreshRequestDto(currentRefreshToken))
                    tokenStorage.saveTokens(refreshed.accessToken, refreshed.refreshToken)
                    buildRetryRequest(response.request, refreshed.accessToken)
                } catch (e: Exception) {
                    tokenStorage.clear()
                    null
                }
            }
        }
    }

    private fun buildRetryRequest(original: Request, newAccessToken: String): Request {
        return original.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}

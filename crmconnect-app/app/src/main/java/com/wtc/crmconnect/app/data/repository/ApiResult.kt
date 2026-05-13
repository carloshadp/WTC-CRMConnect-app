package com.wtc.crmconnect.app.data.repository

import com.squareup.moshi.Moshi
import com.wtc.crmconnect.app.data.remote.dto.error.ErrorResponseDto
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Executa uma chamada suspend de API e devolve `Result<T>`, traduzindo qualquer
 * `HttpException`/`IOException` em [AppException] com mensagem PT-BR.
 *
 * Cancellation exceptions são repropagadas (corrotina cancelada não é falha de domínio).
 */
internal suspend inline fun <T> apiCall(
    moshi: Moshi,
    crossinline block: suspend () -> T
): Result<T> = try {
    Result.success(block())
} catch (ce: kotlinx.coroutines.CancellationException) {
    throw ce
} catch (t: Throwable) {
    Result.failure(AppException(mapApiError(t, moshi)))
}

/**
 * Converte uma `Response<Unit>` (DELETE/204) em sucesso/falha sem propagar `Response`
 * para fora do repositório.
 */
internal fun Response<Unit>.requireSuccess() {
    if (!isSuccessful) throw HttpException(this)
}

internal fun mapApiError(throwable: Throwable, moshi: Moshi): String = when (throwable) {
    is HttpException -> parseHttpError(throwable, moshi)
    is IOException -> "Sem conexão com o servidor. Verifique sua internet."
    is AppException -> throwable.message ?: "Erro inesperado. Tente novamente."
    else -> throwable.message ?: "Erro inesperado. Tente novamente."
}

private fun parseHttpError(e: HttpException, moshi: Moshi): String {
    val raw = e.response()?.errorBody()?.string()
    if (raw.isNullOrBlank()) return defaultMessageFor(e.code())
    return try {
        moshi.adapter(ErrorResponseDto::class.java).fromJson(raw)?.message
            ?: defaultMessageFor(e.code())
    } catch (_: Exception) {
        defaultMessageFor(e.code())
    }
}

private fun defaultMessageFor(code: Int): String = when (code) {
    400 -> "Dados inválidos. Verifique e tente novamente."
    401 -> "Sessão expirada. Faça login novamente."
    403 -> "Você não tem permissão para esta ação."
    404 -> "Recurso não encontrado."
    409 -> "Conflito de dados. Verifique e tente novamente."
    in 500..599 -> "Erro no servidor. Tente novamente em instantes."
    else -> "Erro inesperado (HTTP $code)."
}

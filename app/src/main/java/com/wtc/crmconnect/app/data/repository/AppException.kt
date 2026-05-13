package com.wtc.crmconnect.app.data.repository

/**
 * Exceção de domínio do app. Carrega mensagem em PT-BR pronta para exibir.
 * Repositories envelopam falhas HTTP/IO em [AppException] antes de devolver `Result.failure(...)`.
 */
class AppException(message: String) : RuntimeException(message)

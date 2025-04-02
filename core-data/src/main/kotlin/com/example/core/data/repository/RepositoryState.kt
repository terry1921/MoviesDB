package com.example.core.data.repository

import okhttp3.Response

sealed class RepositoryState<out T> {
    data class Success<T>(val data: T) : RepositoryState<T>()
    data class Error(val code: Int, val error: String) : RepositoryState<Nothing>()
}

fun Response.handleError(): RepositoryState.Error {
    return when(this.code) {
        500 -> RepositoryState.Error(code = 500, error = "Internal Server Error - Server is down")
        503 -> RepositoryState.Error(code = 503, error = "Service Unavailable - Server is down")
        404 -> RepositoryState.Error(code = 404, error = "Not Found - Resource not found")
        401 -> RepositoryState.Error(code = 401, error = "Unauthorized Access - Invalid API Key")
        else -> RepositoryState.Error(
            code = this.code,
            error = "Error response for this code: ${this.code}"
        )
    }
}
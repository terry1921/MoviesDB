package com.example.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

private const val TAGR = "OkHttpRequest"

internal class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Timber.tag(TAGR).d("request -> $originalRequest")
        val response = chain.proceed(originalRequest)
        val responseBodyCopy = response.peekBody(Long.MAX_VALUE).string()
        Timber.tag(TAGR).d( "host -> ${originalRequest.url} response -> $responseBodyCopy")
        return response
    }
}
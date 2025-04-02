package com.example.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

private const val TAG = "OkHttpRequest"

internal class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Timber.tag(TAG).d("request -> $originalRequest")
        val response = chain.proceed(originalRequest)
        val responseBodyCopy = response.peekBody(Long.MAX_VALUE).string()
        Timber.tag(TAG).d( "host -> ${originalRequest.url} response -> $responseBodyCopy")
        return response
    }
}
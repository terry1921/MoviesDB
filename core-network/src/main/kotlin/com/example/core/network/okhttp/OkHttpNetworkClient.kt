package com.example.core.network.okhttp

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

interface NetworkClient {
    fun client(): OkHttpClient
    fun newCall(request: Request): Call
}

class OkHttpNetworkClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
) : NetworkClient {
    override fun client(): OkHttpClient = okHttpClient
    override fun newCall(request: Request): Call {
        return okHttpClient.newCall(request)
    }
}
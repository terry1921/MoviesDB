package com.example.core.network.okhttp

import com.google.gson.Gson
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

interface NetworkClient {
    fun client(): OkHttpClient
    fun gson(): Gson
    fun newCall(request: Request): Call
}

class OkHttpNetworkClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) : NetworkClient {
    override fun client(): OkHttpClient = okHttpClient
    override fun gson(): Gson = gson
    override fun newCall(request: Request): Call {
        return okHttpClient.newCall(request)
    }
}
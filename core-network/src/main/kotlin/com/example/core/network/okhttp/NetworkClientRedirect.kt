package com.example.core.network.okhttp

import com.google.gson.Gson
import okhttp3.OkHttpClient
import javax.inject.Inject

class NetworkClientRedirect @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) {
    fun client(): OkHttpClient = client
    fun gson(): Gson = gson
}
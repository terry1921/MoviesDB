package com.example.core.network.utils

import com.example.core.network.BuildConfig
import com.example.core.network.sources.Endpoints
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

data class RequestDto(
    val method: HttpMethod = HttpMethod.GET,
    val resource: String = "",
    var args: Map<String, Any> = mapOf(),
    var connectTimeout: Int = Constant.TIMEOUT
)

fun RequestDto.getRequest(): Request {
    val url = getUrl()
    val header = mapOf(
        Constant.HEADER_AUTH to "Bearer ${BuildConfig.API_KEY}",
    ).toHeaders()

    return Request.Builder()
        .url(url)
        .method(method.method, body = null)
        .headers(header)
        .build()
}

private fun RequestDto.getUrl(): String {
    var url = "${Endpoints.BASE_URL}$resource"
    try {
        url = setParams(url)
    } catch (e: UnsupportedEncodingException) {
        Timber.tag("Request").e(e.cause, e.message ?: "error convirtiendo url")
    }
    return url
}

private fun RequestDto.setParams(url: String): String {
    val queryParams = args.toSortedMap().map { (key, value) ->
        "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value.toString(), "UTF-8")}"
    }.joinToString("&")
    return if (url.contains("?")) {
        if (queryParams.isEmpty()) {
            url
        } else {
            "$url&$queryParams"
        }
    } else {
        if (queryParams.isEmpty()) {
            url
        } else {
            "$url?$queryParams"
        }
    }
}
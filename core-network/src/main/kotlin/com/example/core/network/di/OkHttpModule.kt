package com.example.core.network.di

import com.example.core.network.interceptor.MyInterceptor
import com.example.core.network.okhttp.NetworkClient
import com.example.core.network.okhttp.OkHttpNetworkClient
import com.example.core.network.utils.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideNetworkClient(
        okHttpClientFactory: OkHttpClientFactory
    ): NetworkClient {
        return OkHttpNetworkClient(okHttpClientFactory.create(isRedirect = false).build())
    }

    @AssistedFactory
    fun interface OkHttpClientFactory {
        fun create(isRedirect: Boolean): OkHttpClientBuilder
    }

    open class OkHttpClientBuilder @AssistedInject constructor(
        @Assisted val isRedirect: Boolean
    ) {
        fun build(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(MyInterceptor())
                .followRedirects(isRedirect)
                .followSslRedirects(isRedirect)
                .connectTimeout(Constant.TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(Constant.TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(Constant.TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()
        }
    }

    @AssistedFactory
    fun interface RetrofitFactory {
        fun create(baseUrl: String): RetrofitBuilder
    }

    open class RetrofitBuilder @AssistedInject constructor(
        @Assisted val baseUrl: String,
        val okHttpClientFactory: OkHttpClientFactory
    ) {
        inline fun <reified T> build(): T {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            return Retrofit.Builder()
                .client(okHttpClientFactory.create(isRedirect = false).build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create()
        }
    }

}
package com.example.core.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val applicationDispatcher: AppDispatcher)

enum class AppDispatcher {
    IO,
    UNCONFINED,
}
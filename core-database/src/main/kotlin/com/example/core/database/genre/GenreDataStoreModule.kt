package com.example.core.database.genre

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GenreDataStoreModule {

    @Binds
    @Singleton
    abstract fun bindGenreDataStore(
        impl: GenreDataStoreImpl
    ): GenreDataStore
}
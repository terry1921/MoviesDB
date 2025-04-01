package com.example.core.data.repository.movies

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal fun interface MovieModule {

    @Binds
    fun bindMoviesRepository(impl: MovieRepositoryImpl): MovieRepository
}

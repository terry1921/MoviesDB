package com.example.core.database.movie


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovieEntityRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMoviesResultRepository(
        impl: MovieRepositoryImpl
    ): MovieEntityRepository
}
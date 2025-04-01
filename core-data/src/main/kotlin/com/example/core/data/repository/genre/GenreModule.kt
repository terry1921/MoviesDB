package com.example.core.data.repository.genre

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface GenreModule {

    @Binds
    fun bindGenreRepository(impl: GenreRepositoryImpl): GenreRepository
}
package com.example.core.data.repository.movies

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    @WorkerThread
    fun getMostPopularMovies(
        page: Int,
    ): Flow<MovieRepositoryState>
}
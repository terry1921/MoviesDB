package com.example.core.data.repository.movies

import androidx.annotation.WorkerThread
import com.example.core.model.movie.MoviesResult
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    @WorkerThread
    fun getMostPopularMovies(
        page: Int,
    ): Flow<MovieRepositoryState>
}

sealed class MovieRepositoryState {
    data class Success(val result: MoviesResult) : MovieRepositoryState()
    data class Error(val error: String) : MovieRepositoryState()
}
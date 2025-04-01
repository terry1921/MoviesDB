package com.example.core.data.repository.movie

import com.example.core.model.movie.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieDetailRepository {
    fun getMovieDetail(movieId: Int): Flow<MovieDetailRepositoryState>
}

sealed class MovieDetailRepositoryState {
    data class Success(val detail: MovieDetail) : MovieDetailRepositoryState()
    data class Error(val error: String) : MovieDetailRepositoryState()
}
package com.example.core.data.repository.movie

import com.example.core.data.repository.RepositoryState
import com.example.core.model.movie.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieDetailRepository {
    fun getMovieDetail(movieId: Int): Flow<RepositoryState<MovieDetail>>
}
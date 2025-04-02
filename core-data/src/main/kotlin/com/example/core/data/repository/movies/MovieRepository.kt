package com.example.core.data.repository.movies

import androidx.annotation.WorkerThread
import com.example.core.data.repository.RepositoryState
import com.example.core.model.movie.MoviesResult
import com.example.core.network.sources.Values
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    @WorkerThread
    fun getMostPopularMovies(
        page: Int,
        sortBy: SortOption
    ): Flow<RepositoryState<MoviesResult>>
}

enum class SortOption(val value: String) {
    POPULARITY_ASC(Values.POPULARITY_ASC),
    POPULARITY_DESC(Values.POPULARITY_DESC),
    VOTE_AVG_ASC(Values.VOTE_AVG_ASC),
    VOTE_AVG_DESC(Values.VOTE_AVG_DESC),
    VOTE_COUNT_ASC(Values.VOTE_COUNT_ASC),
    VOTE_COUNT_DESC(Values.VOTE_COUNT_DESC)
}
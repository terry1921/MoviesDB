package com.example.core.data.repository.genre

import androidx.annotation.WorkerThread
import com.example.core.model.genre.Genre
import com.example.core.model.genre.GenresResponse
import kotlinx.coroutines.flow.Flow

interface GenreRepository {

    @WorkerThread
    fun getGenres(): Flow<GenreRepositoryState>
}

sealed class GenreRepositoryState {
    data class Success(val genres: List<Genre>) : GenreRepositoryState()
    data class Error(val error: String) : GenreRepositoryState()
}

package com.example.core.database.genre

import com.example.core.model.genre.Genre
import kotlinx.coroutines.flow.Flow

interface GenreDataStore {
    val genresFlow: Flow<List<Genre>>
    suspend fun saveGenres(genres: List<Genre>)
    fun getGenreById(id: Int): Flow<Genre?>
}

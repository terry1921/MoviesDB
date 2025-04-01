package com.example.core.database.genre

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.model.genre.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "genres_datastore")

class GenreDataStoreImpl @Inject constructor(
    private val gson: Gson,
    @ApplicationContext private val context: Context
) : GenreDataStore {

    private val GENRES_KEY = stringPreferencesKey("genres_key")

    override val genresFlow: Flow<List<Genre>> = context.dataStore.data.map { preferences ->
        preferences[GENRES_KEY]?.let { json ->
            val type = object : TypeToken<List<Genre>>() {}.type
            gson.fromJson<List<Genre>>(json, type) ?: emptyList()
        } ?: emptyList()
    }

    override suspend fun saveGenres(genres: List<Genre>) {
        context.dataStore.edit { preferences ->
            preferences[GENRES_KEY] = gson.toJson(genres)
        }
    }

    override fun getGenreById(id: Int): Flow<Genre?> = genresFlow.map { genres ->
        genres.firstOrNull { it.id == id }
    }
}

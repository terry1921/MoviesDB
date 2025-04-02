package com.example.core.database.movie


import com.example.core.model.movie.Movie

interface MovieEntityRepository {
    suspend fun saveMovies(movies: List<Movie>)
    suspend fun getMovies(): List<Movie>
    suspend fun getMovieById(id: Int): Movie?
    suspend fun searchMoviesByTitle(title: String): List<Movie>
}
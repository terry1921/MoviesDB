package com.example.core.database.movie

import com.example.core.database.dao.MovieDao
import com.example.core.database.entity.MovieEntity
import com.example.core.model.movie.Movie
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao
) : MovieEntityRepository {

    override suspend fun saveMovies(movies: List<Movie>) {
        val movieEntities = movies.map { movie ->
            MovieEntity(
                id = movie.id,
                adult = movie.adult,
                backdropPath = movie.backdropPath,
                genreIds = movie.genreIds,
                originalLanguage = movie.originalLanguage,
                originalTitle = movie.originalTitle,
                overview = movie.overview,
                popularity = movie.popularity,
                posterPath = movie.posterPath,
                releaseDate = movie.releaseDate,
                title = movie.title,
                video = movie.video,
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount
            )
        }
        movieDao.insertMovies(movieEntities)
    }

    override suspend fun getMovies(): List<Movie> {
        val movieEntities = movieDao.getMovies()
        return movieEntities.map { movieEntity ->
            Movie(
                id = movieEntity.id,
                adult = movieEntity.adult,
                backdropPath = movieEntity.backdropPath,
                genreIds = movieEntity.genreIds,
                originalLanguage = movieEntity.originalLanguage,
                originalTitle = movieEntity.originalTitle,
                overview = movieEntity.overview,
                popularity = movieEntity.popularity,
                posterPath = movieEntity.posterPath,
                releaseDate = movieEntity.releaseDate,
                title = movieEntity.title,
                video = movieEntity.video,
                voteAverage = movieEntity.voteAverage,
                voteCount = movieEntity.voteCount
            )
        }
    }

    override suspend fun getMovieById(id: Int): Movie? {
        val movieEntity = movieDao.getMovieById(id)
        return movieEntity?.let {
            Movie(
                id = it.id,
                adult = it.adult,
                backdropPath = it.backdropPath,
                genreIds = it.genreIds,
                originalLanguage = it.originalLanguage,
                originalTitle = it.originalTitle,
                overview = it.overview,
                popularity = it.popularity,
                posterPath = it.posterPath,
                releaseDate = it.releaseDate,
                title = it.title,
                video = it.video,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount
            )
        }
    }

    override suspend fun searchMoviesByTitle(title: String): List<Movie> {
        val movieEntities = movieDao.searchMoviesByTitle(title)
        return movieEntities.map { movieEntity ->
            Movie(
                id = movieEntity.id,
                adult = movieEntity.adult,
                backdropPath = movieEntity.backdropPath,
                genreIds = movieEntity.genreIds,
                originalLanguage = movieEntity.originalLanguage,
                originalTitle = movieEntity.originalTitle,
                overview = movieEntity.overview,
                popularity = movieEntity.popularity,
                posterPath = movieEntity.posterPath,
                releaseDate = movieEntity.releaseDate,
                title = movieEntity.title,
                video = movieEntity.video,
                voteAverage = movieEntity.voteAverage,
                voteCount = movieEntity.voteCount
            )
        }
    }
}
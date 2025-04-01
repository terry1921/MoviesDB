package com.example.mobilecoding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.repository.genre.GenreRepository
import com.example.core.data.repository.genre.GenreRepositoryState
import com.example.core.data.repository.movies.MovieRepository
import com.example.core.data.repository.movies.MovieRepositoryState
import com.example.core.model.movie.Movie
import com.example.core.model.movie.MoviesResult
import com.example.core.network.AppDispatcher
import com.example.core.network.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
    @Dispatcher(AppDispatcher.IO) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainState> = MutableStateFlow(MainState.Loading)
    val uiState: StateFlow<MainState> = _uiState

    private val _genreMap: MutableStateFlow<Map<Int, String>> = MutableStateFlow(emptyMap())
    val genreMap: StateFlow<Map<Int, String>> = _genreMap

    private val _currentPage = MutableStateFlow(1)
    private val _totalPages = MutableStateFlow(1)
    private val accumulatedMovies = mutableListOf<Movie>()

    fun loadNextPage() {
        if (_currentPage.value > _totalPages.value) return

        viewModelScope.launch(dispatcher) {
            movieRepository.getMostPopularMovies(_currentPage.value)
                .onStart { _uiState.value = MainState.Loading }
                .catch { _uiState.value = MainState.Error(it.message ?: "Error genérico") }
                .collect { result ->
                    when (result) {
                        is MovieRepositoryState.Error -> {
                            _uiState.value = MainState.Error(result.error)
                        }
                        is MovieRepositoryState.Success -> {
                            _totalPages.value = result.result.totalPages
                            accumulatedMovies.addAll(result.result.movies)
                            _uiState.value = MainState.MoviesSuccess(
                                MoviesResult(
                                    totalResults = result.result.totalResults,
                                    totalPages = result.result.totalPages,
                                    movies = accumulatedMovies.toList()
                                )
                            )
                            _currentPage.value = _currentPage.value + 1
                        }
                    }
                }
        }
    }

    fun refresh() {
        _currentPage.value = 1
        _totalPages.value = 1
        accumulatedMovies.clear()
        loadNextPage()
    }

    /**
     * Carga el mapeo de géneros. La implementación del GenreRepository se encarga de
     * verificar si ya existen datos guardados en DataStore y no realiza una llamada remota si ya hay datos.
     */
    fun loadGenresMapping() {
        viewModelScope.launch(dispatcher) {
            genreRepository.getGenres()
                .catch { _uiState.value = MainState.Error(it.message ?: "Error retrieving genres") }
                .collect { result ->
                    when (result) {
                        is GenreRepositoryState.Error -> {
                            _uiState.value = MainState.Error(result.error)
                        }
                        is GenreRepositoryState.Success -> {
                            val map = result.genres.associate { it.id to it.name }
                            _genreMap.value = map
                        }
                    }
                }
        }
    }
}

sealed class MainState {
    data object Loading : MainState()
    data class MoviesSuccess(val result: MoviesResult) : MainState()
    data object GenresSuccess : MainState()
    data class Error(val error: String) : MainState()
}
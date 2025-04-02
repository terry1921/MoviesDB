package com.example.mobilecoding.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.repository.RepositoryState
import com.example.core.data.repository.genre.GenreRepository
import com.example.core.data.repository.movies.MovieRepository
import com.example.core.data.repository.movies.SortOption
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

    private val _sortOption = MutableStateFlow(SortOption.POPULARITY_DESC)

    private val _currentPage = MutableStateFlow(1)
    private val _totalPages = MutableStateFlow(1)
    private val accumulatedMovies = mutableListOf<Movie>()

    fun loadNextPage() {
        if (_currentPage.value > _totalPages.value) return

        viewModelScope.launch(dispatcher) {
            movieRepository.getMostPopularMovies(_currentPage.value, _sortOption.value)
                .onStart { _uiState.value = MainState.Loading }
                .catch { _uiState.value = MainState.Error(it.hashCode(), it.message ?: "Error retrieving movies") }
                .collect { result ->
                    when (result) {
                        is RepositoryState.Error -> {
                            _uiState.value = MainState.Error(result.code, result.error)
                        }

                        is RepositoryState.Success -> {
                            _totalPages.value = result.data.totalPages
                            accumulatedMovies.addAll(result.data.movies)
                            _uiState.value = MainState.MoviesSuccess(
                                MoviesResult(
                                    totalResults = result.data.totalResults,
                                    totalPages = result.data.totalPages,
                                    movies = accumulatedMovies.toList()
                                )
                            )
                            _currentPage.value += 1
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

    fun loadGenresMapping() {
        viewModelScope.launch(dispatcher) {
            genreRepository.getGenres()
                .catch { _uiState.value = MainState.Error(it.hashCode(), it.message ?: "Error retrieving genres") }
                .collect { result ->
                    when (result) {
                        is RepositoryState.Error -> {
                            _uiState.value = MainState.Error(result.code, result.error)
                        }

                        is RepositoryState.Success -> {
                            val map = result.data.associate { it.id to it.name }
                            _genreMap.value = map
                        }
                    }
                }
        }
    }

    fun setSortOption(option: SortOption) = viewModelScope.launch(dispatcher) {
        _sortOption.value = option
        refresh()
    }
}

sealed class MainState {
    data object Loading : MainState()
    data class MoviesSuccess(val result: MoviesResult) : MainState()
    data object GenresSuccess : MainState()
    data class Error(val code: Int, val message: String) : MainState()
}

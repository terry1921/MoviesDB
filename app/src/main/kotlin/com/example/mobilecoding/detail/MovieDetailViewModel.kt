package com.example.mobilecoding.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.repository.RepositoryState
import com.example.core.data.repository.movie.MovieDetailRepository
import com.example.core.model.movie.MovieDetail
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
class MovieDetailViewModel @Inject constructor(
    private val movieDetailRepository: MovieDetailRepository,
    @Dispatcher(AppDispatcher.IO) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _detailState: MutableStateFlow<MovieDetailState> =
        MutableStateFlow(MovieDetailState.Loading)
    val detailState: StateFlow<MovieDetailState> = _detailState

    fun getMovieDetail(movieId: Int) {
        viewModelScope.launch(dispatcher) {
            movieDetailRepository.getMovieDetail(movieId)
                .onStart { _detailState.value = MovieDetailState.Loading }
                .catch { _detailState.value = MovieDetailState.Error(it.message ?: "Error retrieving movie detail") }
                .collect { result ->
                    when (result) {
                        is RepositoryState.Success -> {
                            _detailState.value = MovieDetailState.Success(result.data)
                        }
                        is RepositoryState.Error -> {
                            _detailState.value = MovieDetailState.Error(result.error)
                        }
                    }
                }
        }
    }
}

sealed class MovieDetailState {
    data object Loading : MovieDetailState()
    data class Success(val detail: MovieDetail) : MovieDetailState()
    data class Error(val error: String) : MovieDetailState()
}

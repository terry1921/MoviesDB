package com.example.core.data.repository.movies

import com.example.core.model.movie.MoviesResponse
import com.example.core.model.movie.MoviesResult
import com.example.core.network.AppDispatcher
import com.example.core.network.Dispatcher
import com.example.core.network.okhttp.NetworkClient
import com.example.core.network.sources.Endpoints
import com.example.core.network.sources.Parameters
import com.example.core.network.sources.Values
import com.example.core.network.utils.HttpMethod
import com.example.core.network.utils.RequestDto
import com.example.core.network.utils.getRequest
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@VisibleForTesting
class MovieRepositoryImpl @Inject constructor(
    private val network: NetworkClient,
    private val gson: Gson,
    @Dispatcher(AppDispatcher.IO) private val dispatcher: CoroutineDispatcher
) : MovieRepository {

    override fun getMostPopularMovies(
        page: Int,
        sortBy: SortOption
    ): Flow<MovieRepositoryState> = flow {
        val dto = RequestDto(
            method = HttpMethod.GET,
            resource = Endpoints.MOVIES_LIST,
            args = mapOf(
                Parameters.INCLUDE_ADULT to Values.FALSE,
                Parameters.INCLUDE_VIDEO to Values.FALSE,
                Parameters.LANGUAGE to Values.ENGLISH,
                Parameters.PAGE to page.toString(),
                Parameters.SORT_BY to sortBy.value,
            ),
        )
        val response = network.newCall(dto.getRequest()).execute()
        if (response.isSuccessful) {
            val body = response.peekBody(Long.MAX_VALUE).string()
            if (body.isNotEmpty()) {
                val moviesResponse = gson.fromJson(body, MoviesResponse::class.java)
                val moviesResult = MoviesResult(
                    totalResults = moviesResponse.totalResults,
                    totalPages = moviesResponse.totalPages,
                    movies = moviesResponse.results
                )
                emit(MovieRepositoryState.Success(moviesResult))
            } else {
                emit(MovieRepositoryState.Error("Error en el cuerpo de la respuesta"))
            }
        } else {
            emit(MovieRepositoryState.Error("Error en la respuesta: ${response.code}"))
        }
    }.flowOn(dispatcher)
}

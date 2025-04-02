package com.example.core.data.repository.movie

import com.example.core.data.repository.RepositoryState
import com.example.core.data.repository.handleError
import com.example.core.model.movie.MovieDetail
import com.example.core.network.AppDispatcher
import com.example.core.network.Dispatcher
import com.example.core.network.okhttp.NetworkClient
import com.example.core.network.sources.Endpoints
import com.example.core.network.utils.HttpMethod
import com.example.core.network.utils.RequestDto
import com.example.core.network.utils.getRequest
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
class MovieDetailRepositoryImpl @Inject constructor(
    private val network: NetworkClient,
    private val gson: Gson,
    @Dispatcher(AppDispatcher.IO) private val dispatcher: CoroutineDispatcher
) : MovieDetailRepository {

    override fun getMovieDetail(movieId: Int): Flow<RepositoryState<MovieDetail>> = flow {
        val endpoint = Endpoints.MOVIE_DETAIL.replace("{movie_id}", movieId.toString())
        val dto = RequestDto(
            method = HttpMethod.GET,
            resource = endpoint
        )
        val response = network.newCall(dto.getRequest()).execute()
        if (response.isSuccessful) {
            val body = response.body?.string()
            if (body != null) {
                val detail = gson.fromJson(body, MovieDetail::class.java)
                emit(RepositoryState.Success(detail))
            } else {
                emit(RepositoryState.Error(
                    code = 1,
                    error = "El cuerpo de la respuesta es nulo"))
            }
        } else {
            emit(response.handleError())
        }
    }.flowOn(dispatcher)
}
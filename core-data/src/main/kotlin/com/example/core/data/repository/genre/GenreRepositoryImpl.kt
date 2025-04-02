package com.example.core.data.repository.genre

import com.example.core.data.repository.RepositoryState
import com.example.core.data.repository.handleError
import com.example.core.database.genre.GenreDataStore
import com.example.core.model.genre.GenresResponse
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@VisibleForTesting
class GenreRepositoryImpl @Inject constructor(
    private val network: NetworkClient,
    private val gson: Gson,
    private val genreDataStore: GenreDataStore,
    @Dispatcher(AppDispatcher.IO) private val dispatcher: CoroutineDispatcher
) : GenreRepository {

    override fun getGenres() = flow {
        val cachedGenres = genreDataStore.genresFlow.first()
        if (cachedGenres.isNotEmpty()) {
            emit(RepositoryState.Success(cachedGenres))
        } else {
            val dto = RequestDto(
                method = HttpMethod.GET,
                resource = Endpoints.GENRE_LIST,
                args = mapOf(
                    Parameters.LANGUAGE to Values.ENGLISH
                )
            )
            val response = network.newCall(dto.getRequest()).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (body != null) {
                    val genresResponse = gson.fromJson(body, GenresResponse::class.java)
                    genreDataStore.saveGenres(genresResponse.genres)
                    emit(RepositoryState.Success(genresResponse.genres))
                } else {
                    emit(RepositoryState.Error(
                        code = 1,
                        error = "Error en el cuerpo de la respuesta")
                    )
                }
            } else {
                emit(response.handleError())
            }
        }
    }.flowOn(dispatcher)

}

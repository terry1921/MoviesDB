package com.example.core.data.repository.genre

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
import kotlinx.coroutines.flow.Flow
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

    override fun getGenres(): Flow<GenreRepositoryState> = flow {
        val cachedGenres = genreDataStore.genresFlow.first()
        if (cachedGenres.isNotEmpty()) {
            emit(GenreRepositoryState.Success(cachedGenres))
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
                    emit(GenreRepositoryState.Success(genresResponse.genres))
                } else {
                    emit(GenreRepositoryState.Error("Error en el cuerpo de la respuesta"))
                }
            } else {
                emit(GenreRepositoryState.Error("Error en la respuesta: ${response.code}"))
            }
        }
    }.flowOn(dispatcher)
}

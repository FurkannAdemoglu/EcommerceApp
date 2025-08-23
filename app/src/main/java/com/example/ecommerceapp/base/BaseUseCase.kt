package com.example.ecommerceapp.base

import com.example.ecommerceapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import retrofit2.HttpException

abstract class BaseUseCase<in P, R> {

    operator fun invoke(param: P?): Flow<Resource<R?>> = flow {
        emit(Resource.Loading)
        val result = execute(param)
        emit(Resource.Success(result))
    }
        .catch { e ->
            when (e) {
                is HttpException -> {
                    emit(Resource.Error("Sunucu hatası: ${e.code()}"))
                }

                is IOException -> {
                    emit(Resource.Error("İnternet bağlantısı hatası"))
                }

                else -> {
                    emit(Resource.Error(e.message ?: "Bilinmeyen hata"))
                }
            }
        }
        .flowOn(Dispatchers.IO)


    protected abstract suspend fun execute(param: P? = null): R
}

abstract class BaseUseCaseNoParameter<R> {

    operator fun invoke(): Flow<Resource<R?>> = flow {
        emit(Resource.Loading)
        val result = execute()
        emit(Resource.Success(result))
    }
        .catch { e ->
            when (e) {
                is HttpException -> {
                    emit(Resource.Error("Sunucu hatası: ${e.code()}"))
                }

                is IOException -> {
                    emit(Resource.Error("İnternet bağlantısı hatası"))
                }

                else -> {
                    emit(Resource.Error(e.message ?: "Bilinmeyen hata"))
                }
            }
        }
        .flowOn(Dispatchers.IO)


    protected abstract suspend fun execute(): R
}

abstract class BaseUseCaseNoParameterFlow<R> {

    operator fun invoke(): Flow<Resource<R?>> = flow {
        emit(Resource.Loading)
        execute().collect { result ->
            emit(Resource.Success(result))
        }
    }
        .catch { e ->
            when (e) {
                is HttpException -> {
                    emit(Resource.Error("Sunucu hatası: ${e.code()}"))
                }

                is IOException -> {
                    emit(Resource.Error("İnternet bağlantısı hatası"))
                }

                else -> {
                    emit(Resource.Error(e.message ?: "Bilinmeyen hata"))
                }
            }
        }
        .flowOn(Dispatchers.IO)


    protected abstract suspend fun execute(): Flow<R>
}
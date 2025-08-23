package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.datasource.local.favorite.FavoriteLocalDataSource
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val localDataSource: FavoriteLocalDataSource
):FavoriteRepository{
    override suspend fun insertFavorite(favoriteProduct: FavoriteProduct) {
        localDataSource.insertFavorite(favoriteProduct)
    }

    override suspend fun deleteFavorite(favoriteProduct: FavoriteProduct) {
       localDataSource.deleteFavorite(favoriteProduct)
    }

    override suspend fun getAllFavorites(): Flow<List<FavoriteProduct>> {
        return localDataSource.getAllFavorites()
    }
}
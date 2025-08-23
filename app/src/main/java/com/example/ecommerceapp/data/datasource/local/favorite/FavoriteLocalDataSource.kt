package com.example.ecommerceapp.data.datasource.local.favorite

import com.example.ecommerceapp.domain.model.FavoriteProduct
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {
    suspend fun insertFavorite(favoriteProduct: FavoriteProduct)
    suspend fun deleteFavorite(favoriteProduct: FavoriteProduct)
    suspend fun getAllFavorites(): Flow<List<FavoriteProduct>>
}
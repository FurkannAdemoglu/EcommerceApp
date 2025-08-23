package com.example.ecommerceapp.domain.repository

import com.example.ecommerceapp.domain.model.FavoriteProduct
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun insertFavorite(favoriteProduct: FavoriteProduct)
    suspend fun deleteFavorite(favoriteProduct: FavoriteProduct)
    suspend fun getAllFavorites(): Flow<List<FavoriteProduct>>
}
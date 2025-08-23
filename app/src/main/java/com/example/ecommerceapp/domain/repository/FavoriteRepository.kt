package com.example.ecommerceapp.domain.repository

import com.example.ecommerceapp.domain.model.FavoriteProduct

interface FavoriteRepository {
    suspend fun insertFavorite(favoriteProduct: FavoriteProduct)
    suspend fun deleteFavorite(favoriteProduct: FavoriteProduct)
    suspend fun getAllFavorites():List<FavoriteProduct>
}
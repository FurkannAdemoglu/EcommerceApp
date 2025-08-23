package com.example.ecommerceapp.data.datasource.local.favorite

import com.example.ecommerceapp.domain.model.FavoriteProduct

interface FavoriteLocalDataSource {
    suspend fun insertFavorite(favoriteProduct: FavoriteProduct)
    suspend fun deleteFavorite(favoriteProduct: FavoriteProduct)
    suspend fun getAllFavorites():List<FavoriteProduct>
}
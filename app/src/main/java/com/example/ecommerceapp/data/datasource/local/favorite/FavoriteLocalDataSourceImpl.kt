package com.example.ecommerceapp.data.datasource.local.favorite

import com.example.ecommerceapp.data.local.FavoriteDao
import com.example.ecommerceapp.domain.model.FavoriteProduct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteLocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
): FavoriteLocalDataSource {
    override suspend fun insertFavorite(favoriteProduct: FavoriteProduct) {
        favoriteDao.insertFavorite(favoriteProduct)
    }

    override suspend fun deleteFavorite(favoriteProduct: FavoriteProduct) {
        favoriteDao.deleteFavorite(favoriteProduct)
    }

    override suspend fun getAllFavorites(): List<FavoriteProduct> {
       return favoriteDao.getAllFavorites()
    }
}
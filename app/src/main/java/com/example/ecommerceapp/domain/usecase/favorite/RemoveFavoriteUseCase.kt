package com.example.ecommerceapp.domain.usecase.favorite

import com.example.ecommerceapp.base.BaseUseCase
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) :BaseUseCase<FavoriteProduct,Unit>(){
    override suspend fun execute(param: FavoriteProduct?) {
        if (param != null) {
            favoriteRepository.deleteFavorite(param)
        }
    }
}
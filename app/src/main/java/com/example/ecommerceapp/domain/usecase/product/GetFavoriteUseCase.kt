package com.example.ecommerceapp.domain.usecase.product

import com.example.ecommerceapp.base.BaseUseCaseNoParameter
import com.example.ecommerceapp.base.BaseUseCaseNoParameterFlow
import com.example.ecommerceapp.data.mapper.ProductMapper
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.CartRepository
import com.example.ecommerceapp.domain.repository.FavoriteRepository
import com.example.ecommerceapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val productMapper: ProductMapper
) : BaseUseCaseNoParameter<List<Product>>() {
    override suspend fun execute(): List<Product> {
        val productList = repository.getProducts().map {
            productMapper.mapFromDto(it)
        }
        productList.map { product ->
            product.isFavorite =
                favoriteRepository.getAllFavorites().map { it.id }.toSet().contains(product.id)
        }
        return productList
    }

}
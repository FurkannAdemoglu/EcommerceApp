package com.example.ecommerceapp.domain.usecase.product

import com.example.ecommerceapp.base.BaseUseCaseNoParameterFlow
import com.example.ecommerceapp.data.mapper.ProductMapper
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.FavoriteRepository
import com.example.ecommerceapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val productMapper: ProductMapper
) : BaseUseCaseNoParameterFlow<List<Product>>() {
    override suspend fun execute(): Flow<List<Product>> = flow {
        val products = repository.getProducts().map { productMapper.mapFromDto(it) }
        emit(products)
    }.combine(favoriteRepository.getAllFavorites()) { products, favorites ->
        val favoriteIds = favorites.map { it.id }.toSet()
        products.onEach { product ->
            product.isFavorite = favoriteIds.contains(product.id)
        }
    }

}
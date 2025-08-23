package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.datasource.remote.ProductRemoteDataSourceImpl
import com.example.ecommerceapp.data.remote.dto.ProductDto
import com.example.ecommerceapp.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSourceImpl,
) : ProductRepository {
    override suspend fun getProducts():List<ProductDto> {
        return remoteDataSource.getProducts()
    }
}
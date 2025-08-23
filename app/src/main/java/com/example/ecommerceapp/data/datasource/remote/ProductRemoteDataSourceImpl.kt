package com.example.ecommerceapp.data.datasource.remote

import com.example.ecommerceapp.data.remote.api.ProductApiService
import com.example.ecommerceapp.data.remote.dto.ProductDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRemoteDataSourceImpl @Inject constructor(
    private val apiService: ProductApiService
) :ProductRemoteDataSource{
    override suspend fun getProducts():List<ProductDto>{
        return apiService.getAllProducts()
    }
}
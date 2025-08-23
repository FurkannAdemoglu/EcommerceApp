package com.example.ecommerceapp.data.datasource.remote

import com.example.ecommerceapp.data.remote.dto.ProductDto

interface ProductRemoteDataSource {
    suspend fun getProducts(): List<ProductDto>
}
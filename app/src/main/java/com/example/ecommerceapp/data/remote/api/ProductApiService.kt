package com.example.ecommerceapp.data.remote.api

import com.example.ecommerceapp.data.remote.dto.ProductDto
import retrofit2.http.GET

interface ProductApiService {
    @GET("products")
    suspend fun getAllProducts():List<ProductDto>
}
package com.example.ecommerceapp.domain.repository

import com.example.ecommerceapp.data.remote.dto.ProductDto
import com.example.ecommerceapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts():List<ProductDto>
}
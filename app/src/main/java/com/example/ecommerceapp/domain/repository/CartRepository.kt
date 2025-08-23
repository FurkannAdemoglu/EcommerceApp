package com.example.ecommerceapp.domain.repository

import com.example.ecommerceapp.domain.model.CartProduct
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun insertCart(cartProduct: CartProduct)
    suspend fun updateCart(cartProduct: CartProduct)
    suspend fun deleteCart(cartProduct: CartProduct)
    suspend fun getAllCartProducts(): Flow<List<CartProduct>>
    suspend fun deleteAllProducts(productList:List<CartProduct>)
}
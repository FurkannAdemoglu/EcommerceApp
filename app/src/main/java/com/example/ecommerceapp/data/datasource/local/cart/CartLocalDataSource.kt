package com.example.ecommerceapp.data.datasource.local.cart

import com.example.ecommerceapp.domain.model.CartProduct
import kotlinx.coroutines.flow.Flow

interface CartLocalDataSource {
    suspend fun insertCart(cartProduct: CartProduct)
    suspend fun updateCart(cartProduct: CartProduct)
    suspend fun deleteCart(cartProduct: CartProduct)
    suspend fun getAllCartProducts(): Flow<List<CartProduct>>
    suspend fun deleteAll(productList:List<CartProduct>)
}
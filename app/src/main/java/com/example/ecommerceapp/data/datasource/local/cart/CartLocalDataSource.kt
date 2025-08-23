package com.example.ecommerceapp.data.datasource.local.cart

import com.example.ecommerceapp.domain.model.CartProduct

interface CartLocalDataSource {
    suspend fun insertCart(cartProduct: CartProduct)
    suspend fun updateCart(cartProduct: CartProduct)
    suspend fun deleteCart(cartProduct: CartProduct)
    suspend fun getAllCartProducts():List<CartProduct>
    suspend fun deleteAll(productList:List<CartProduct>)
}
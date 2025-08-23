package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.datasource.local.cart.CartLocalDataSource
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.repository.CartRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartLocalDataSource: CartLocalDataSource
):CartRepository{
    override suspend fun insertCart(cartProduct: CartProduct) {
        cartLocalDataSource.insertCart(cartProduct)
    }

    override suspend fun updateCart(cartProduct: CartProduct) {
        cartLocalDataSource.updateCart(cartProduct)
    }

    override suspend fun deleteCart(cartProduct: CartProduct) {
       cartLocalDataSource.deleteCart(cartProduct)
    }

    override suspend fun getAllCartProducts(): List<CartProduct> {
       return cartLocalDataSource.getAllCartProducts()
    }

    override suspend fun deleteAllProducts(productList: List<CartProduct>) {
        cartLocalDataSource.deleteAll(productList)
    }
}
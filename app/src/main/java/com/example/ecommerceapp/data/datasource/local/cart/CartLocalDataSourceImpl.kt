package com.example.ecommerceapp.data.datasource.local.cart

import com.example.ecommerceapp.data.local.CartDao
import com.example.ecommerceapp.domain.model.CartProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartLocalDataSourceImpl @Inject constructor(
    private val cartDao: CartDao
) : CartLocalDataSource {
    override suspend fun insertCart(cartProduct: CartProduct) {
        val existingItem = cartDao.getCartProductById(cartProduct.id)
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            cartDao.updateCart(updatedItem)
        } else {
            cartDao.insertCart(cartProduct)
        }
    }

    override suspend fun updateCart(cartProduct: CartProduct) {
        cartDao.updateCart(cartProduct)
    }

    override suspend fun deleteCart(cartProduct: CartProduct) {
        val existing = cartDao.getCartProductById(cartProduct.id)
        if (existing != null) {
            if (existing.quantity > 1) {
                cartDao.decreaseQuantity(cartProduct.id)
            } else {
                cartDao.deleteCart(cartProduct)
            }
        }
    }

    override suspend fun getAllCartProducts(): Flow<List<CartProduct>> {
        return cartDao.getAllCartProduct()
    }

    override suspend fun deleteAll(productList: List<CartProduct>) {
        cartDao.deleteAll(productList)
    }
}
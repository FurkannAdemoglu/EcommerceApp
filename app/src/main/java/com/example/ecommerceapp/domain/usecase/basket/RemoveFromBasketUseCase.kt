package com.example.ecommerceapp.domain.usecase.basket

import com.example.ecommerceapp.base.BaseUseCase
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromBasketUseCase @Inject constructor(
    private val cartRepository: CartRepository
) :BaseUseCase<CartProduct,Unit>(){
    override suspend fun execute(param: CartProduct?) {
        if (param != null) {
            cartRepository.deleteCart(param)
        }
    }
}
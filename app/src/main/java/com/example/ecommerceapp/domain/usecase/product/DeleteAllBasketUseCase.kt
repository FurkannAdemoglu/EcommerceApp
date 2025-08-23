package com.example.ecommerceapp.domain.usecase.product

import com.example.ecommerceapp.base.BaseUseCase
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.repository.CartRepository
import javax.inject.Inject

class DeleteAllBasketUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : BaseUseCase<List<CartProduct>, Unit>(){

    override suspend fun execute(param: List<CartProduct>?) {
        if (param != null) {
            cartRepository.deleteAllProducts(param)
        }
    }
}
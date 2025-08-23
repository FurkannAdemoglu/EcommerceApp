package com.example.ecommerceapp.domain.usecase.product

import com.example.ecommerceapp.base.BaseUseCaseNoParameter
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.repository.CartRepository
import javax.inject.Inject

class GetBasketProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : BaseUseCaseNoParameter<List<CartProduct>>(){
    override suspend fun execute(): List<CartProduct> {
       return cartRepository.getAllCartProducts()
    }
}
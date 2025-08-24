package com.example.ecommerceapp.domain.usecase.basket

import com.example.ecommerceapp.base.BaseUseCaseNoParameterFlow
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBasketProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : BaseUseCaseNoParameterFlow<List<CartProduct>>(){
    override suspend fun execute(): Flow<List<CartProduct>> {
       return cartRepository.getAllCartProducts()
    }
}
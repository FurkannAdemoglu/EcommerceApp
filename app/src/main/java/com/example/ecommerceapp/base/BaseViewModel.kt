package com.example.ecommerceapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.usecase.product.GetBasketProductUseCase
import com.example.ecommerceapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BaseViewModel(
    private val getBasketProductUseCase: GetBasketProductUseCase
) : ViewModel() {

    protected val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount

    fun loadCartItemCount() {
        viewModelScope.launch {
            getBasketProductUseCase.invoke().collect { response ->
                when (response) {
                    is Resource.Success -> {
                        val totalCount = response.data?.sumOf { it.quantity } ?: 0
                        _cartItemCount.value = totalCount
                    }

                    else -> Unit
                }
            }
        }
    }
}
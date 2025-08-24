package com.example.ecommerceapp.presentation.ui.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.usecase.product.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.DeleteAllBasketUseCase
import com.example.ecommerceapp.domain.usecase.product.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.RemoveFromBasketUseCase
import com.example.ecommerceapp.presentation.ui.basket.adapter.viewitem.BasketListViewItem
import com.example.ecommerceapp.utils.Resource
import com.example.ecommerceapp.utils.totalPriceFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    private val addToBasketProductUseCase: AddToBasketProductUseCase,
    private val removeToBasketProductUseCase: RemoveFromBasketUseCase,
    private val getBasketProductUseCase: GetBasketProductUseCase,
    private val deleteAllBasketUseCase: DeleteAllBasketUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<BasketListUiState>(BasketListUiState.Loading)
    val uiState: StateFlow<BasketListUiState> = _uiState.asStateFlow()
    val productList = mutableListOf<BasketListViewItem>()
    val cartList = mutableListOf<CartProduct>()

    init {
        getCartProductList()
    }

    private fun getCartProductList() {
        viewModelScope.launch {
            getBasketProductUseCase.invoke().collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        _uiState.value = BasketListUiState.Loading
                    }

                    is Resource.Success -> {
                        productList.clear()
                        cartList.clear()
                        if (response.data.isNullOrEmpty()){
                            _uiState.value = BasketListUiState.EmptySuccess
                        }else{
                            response.data.map { product ->
                                productList.add(BasketListViewItem.ItemProductBasketListViewItem(product))
                            }
                            cartList.addAll(response.data)
                            _uiState.value = BasketListUiState.Success(productList,response.data.totalPriceFormatted().toString())
                        }
                    }

                    is Resource.Error -> {
                        _uiState.value = BasketListUiState.Error(response.message)
                    }
                }
            }
        }
    }

    fun addToBasket(cartProduct: CartProduct) {
        viewModelScope.launch {
            addToBasketProductUseCase(cartProduct).collect { response ->
                when (response) {
                    is Resource.Error -> {
                        _uiState.value = BasketListUiState.Error(response.message)
                    }

                    Resource.Loading -> {
                        _uiState.value = BasketListUiState.Loading
                    }

                    is Resource.Success<*> -> {
                        getCartProductList()
                    }
                }
            }
        }
    }

    fun removeFromBasket(cartProduct: CartProduct) {
        viewModelScope.launch {
            removeToBasketProductUseCase(cartProduct).collect { response ->
                when (response) {
                    is Resource.Error -> {
                        _uiState.value = BasketListUiState.Error(response.message)
                    }

                    Resource.Loading -> {
                        _uiState.value = BasketListUiState.Loading
                    }

                    is Resource.Success<*> -> {
                        getCartProductList()
                    }
                }
            }
        }
    }

    fun deleteAllBasket() {
        viewModelScope.launch {
            deleteAllBasketUseCase.invoke(cartList).collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        _uiState.value = BasketListUiState.Loading
                    }

                    is Resource.Success -> {
                        _uiState.value = BasketListUiState.DeleteSuccess
                        getCartProductList()
                    }

                    is Resource.Error -> {
                        _uiState.value = BasketListUiState.Error(response.message)
                    }
                }
            }
        }

    }

}

sealed interface BasketListUiState {
    data object Loading : BasketListUiState
    data class Success(val cartProductList: List<BasketListViewItem>?,val totalPrice:String) : BasketListUiState
    data object EmptySuccess : BasketListUiState
    data object DeleteSuccess: BasketListUiState
    data class Error(val message: String) : BasketListUiState
}
package com.example.ecommerceapp.presentation.ui.product.detail

import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.base.BaseViewModel
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.favorite.AddFavoriteProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.favorite.RemoveFavoriteUseCase
import com.example.ecommerceapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val addFavoriteProductUseCase: AddFavoriteProductUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addToBasketProductUseCase: AddToBasketProductUseCase,
    private val getBasketProductUseCase: GetBasketProductUseCase
) :BaseViewModel(getBasketProductUseCase){
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Empty)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            if (product.isFavorite) {
                removeFavoriteUseCase(FavoriteProduct(product.id)).collect{response->
                    when(response){
                        is Resource.Error -> {
                            _uiState.value = ProductDetailUiState.Error(response.message)
                        }
                        Resource.Loading -> {
                            _uiState.value = ProductDetailUiState.Loading
                        }
                        is Resource.Success<*> -> {
                            _uiState.value = ProductDetailUiState.RemoveFavorite
                        }
                    }
                }
            } else {
                addFavoriteProductUseCase(FavoriteProduct(product.id)).collect{response->
                    when(response){
                        is Resource.Error -> {
                            _uiState.value = ProductDetailUiState.Error(response.message)
                        }
                        Resource.Loading -> {
                            _uiState.value = ProductDetailUiState.Loading
                        }
                        is Resource.Success<*> -> {
                            _uiState.value = ProductDetailUiState.AddedFavorite
                        }
                    }
                }
            }

        }
    }

    fun addToCart(product: Product) {
        val current = product
        viewModelScope.launch {
            addToBasketProductUseCase(
                CartProduct(
                    current.id,
                    current.name,
                    current.price,
                    1
                )
            ).collect {response->
            when(response){
                is Resource.Error -> {
                    _uiState.value = ProductDetailUiState.Error(response.message)
                }
                Resource.Loading -> {
                    _uiState.value = ProductDetailUiState.Loading
                }
                is Resource.Success -> {
                    _uiState.value = ProductDetailUiState.AddedBasket
                    loadCartItemCount()
                }
            }
            }
        }
    }
}

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data object AddedBasket : ProductDetailUiState
    data object AddedFavorite:ProductDetailUiState
    data object RemoveFavorite:ProductDetailUiState
    data object Empty:ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}
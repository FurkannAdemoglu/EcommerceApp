package com.example.ecommerceapp.presentation.ui.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.product.AddFavoriteProductUseCase
import com.example.ecommerceapp.domain.usecase.product.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.RemoveFavoriteUseCase
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
    private val addToBasketProductUseCase: AddToBasketProductUseCase
) :ViewModel(){
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Empty)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun setProduct(product: Product) {
        _product.value = product
    }

    fun toggleFavorite() {
        val current = _product.value ?: return
        viewModelScope.launch {
            if (current.isFavorite) {
                removeFavoriteUseCase(FavoriteProduct(current.id)).collect{response->
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
                addFavoriteProductUseCase(FavoriteProduct(current.id)).collect{response->
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
            _product.value = current.copy(isFavorite = !current.isFavorite)
        }
    }

    fun addToCart() {
        val current = _product.value ?: return
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
                is Resource.Success<*> -> {
                    _uiState.value = ProductDetailUiState.AddedBasket
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
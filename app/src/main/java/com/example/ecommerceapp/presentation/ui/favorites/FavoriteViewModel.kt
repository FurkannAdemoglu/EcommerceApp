package com.example.ecommerceapp.presentation.ui.favorites

import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.base.BaseViewModel
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.product.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.GetFavoriteUseCase
import com.example.ecommerceapp.domain.usecase.product.RemoveFavoriteUseCase
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addToBasketProductUseCase: AddToBasketProductUseCase,
    private val getBasketProductUseCase: GetBasketProductUseCase
) : BaseViewModel(getBasketProductUseCase) {
    private val _uiState = MutableStateFlow<FavoriteListUiState>(FavoriteListUiState.Loading)
    val uiState: StateFlow<FavoriteListUiState> = _uiState.asStateFlow()
    val productList = mutableListOf<ProductListViewItem>()


    fun getProductList() {
        viewModelScope.launch(Dispatchers.IO) {
            getFavoriteUseCase.invoke().collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        _uiState.value = FavoriteListUiState.Loading
                    }
                    is Resource.Success -> {
                        productList.clear()
                        if (response.data.isNullOrEmpty()) {
                            _uiState.value = FavoriteListUiState.EmptyFavorite
                        } else {
                            response.data.map { product ->
                                productList.add(ProductListViewItem.ItemProductListViewItem(product))
                            }
                            _uiState.value = FavoriteListUiState.Success(productList)
                        }
                    }

                    is Resource.Error -> {
                        _uiState.value = FavoriteListUiState.Error(response.message)
                    }
                }
            }
        }
    }


    fun addToCart(product: Product) {
        viewModelScope.launch {
            addToBasketProductUseCase(
                CartProduct(
                    product.id,
                    product.name,
                    product.price,
                    1
                )
            ).collect { response ->
                when (response) {
                    is Resource.Error -> {
                        _uiState.value = FavoriteListUiState.Error(response.message)
                    }

                    Resource.Loading -> {
                        _uiState.value = FavoriteListUiState.Loading
                    }

                    is Resource.Success -> {
                        _uiState.value = FavoriteListUiState.AddedBasket
                        loadCartItemCount()
                    }
                }
            }
        }
    }

    fun removeFromFavorite(id:String){
        viewModelScope.launch {
            removeFavoriteUseCase.invoke(FavoriteProduct(id)).collect{

            }
        }
    }

    fun dispose(){
        _uiState.value = FavoriteListUiState.Empty
    }


}

sealed interface FavoriteListUiState {
    data object Loading : FavoriteListUiState
    data class Success(val productList: List<ProductListViewItem>?) : FavoriteListUiState
    data object RemoveFavorite : FavoriteListUiState
    data object EmptyFavorite : FavoriteListUiState
    data object Empty : FavoriteListUiState
    data object AddedBasket : FavoriteListUiState
    data class Error(val message: String) : FavoriteListUiState
}
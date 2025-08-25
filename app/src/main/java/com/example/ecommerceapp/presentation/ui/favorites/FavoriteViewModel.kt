package com.example.ecommerceapp.presentation.ui.favorites

import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.base.BaseViewModel
import com.example.ecommerceapp.di.IoDispatcher
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.basket.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.favorite.GetFavoriteUseCase
import com.example.ecommerceapp.domain.usecase.favorite.RemoveFavoriteUseCase
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addToBasketProductUseCase: AddToBasketProductUseCase,
    private val getBasketProductUseCase: GetBasketProductUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(getBasketProductUseCase) {
    private val _uiState = MutableStateFlow<FavoriteListUiState>(FavoriteListUiState.Loading)
    val uiState: StateFlow<FavoriteListUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FavoriteListUiEvent>()
    val uiEvent: SharedFlow<FavoriteListUiEvent> = _uiEvent

    val productList = mutableListOf<ProductListViewItem>()

    private var isFavoriteJob = false


    fun getProductList() {
        viewModelScope.launch(ioDispatcher) {
            getFavoriteUseCase.invoke().collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        _uiState.value = FavoriteListUiState.Loading
                    }
                    is Resource.Success -> {
                        if (!isFavoriteJob){
                            productList.clear()
                            if (response.data.isNullOrEmpty()) {
                                _uiState.value = FavoriteListUiState.EmptyFavorite
                            } else {
                                response.data.map { product ->
                                    productList.add(ProductListViewItem.ItemProductListViewItem(product))
                                }
                                _uiState.value = FavoriteListUiState.Success(productList)
                            }
                        }else{
                            isFavoriteJob = false
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
        isFavoriteJob = true
        viewModelScope.launch {
            removeFavoriteUseCase.invoke(FavoriteProduct(id)).collect{response->
                when (response) {
                    is Resource.Error ->Unit

                    Resource.Loading -> Unit

                    is Resource.Success -> {
                        _uiEvent.emit(FavoriteListUiEvent.RemovedFavorite)
                        loadCartItemCount()
                    }
                }
            }
        }
    }

    fun dispose(){
        _uiState.value = FavoriteListUiState.Empty
    }
}

sealed interface FavoriteListUiEvent {
    data object RemovedFavorite: FavoriteListUiEvent
}

sealed interface FavoriteListUiState {
    data object Loading : FavoriteListUiState
    data class Success(val productList: List<ProductListViewItem>?) : FavoriteListUiState
    data object EmptyFavorite : FavoriteListUiState
    data object Empty : FavoriteListUiState
    data object AddedBasket : FavoriteListUiState
    data class Error(val message: String) : FavoriteListUiState
}
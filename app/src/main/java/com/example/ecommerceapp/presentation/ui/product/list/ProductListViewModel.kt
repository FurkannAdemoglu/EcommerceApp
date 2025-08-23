package com.example.ecommerceapp.presentation.ui.product.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.product.AddFavoriteProductUseCase
import com.example.ecommerceapp.domain.usecase.product.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.GetProductsUseCase
import com.example.ecommerceapp.domain.usecase.product.RemoveFavoriteUseCase
import com.example.ecommerceapp.presentation.ui.product.detail.ProductDetailUiState
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val addFavoriteProductUseCase: AddFavoriteProductUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addToBasketProductUseCase: AddToBasketProductUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    val productList = mutableListOf<ProductListViewItem>()
    private val pageSize = 4
    private var currentIndex = 0

    init {
        getProductList()
    }

    private fun getProductList() {
        viewModelScope.launch(Dispatchers.IO) {
            getProductsUseCase.invoke().collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        _uiState.value = ProductListUiState.Loading
                    }

                    is Resource.Success -> {
                        productList.clear()
                        response.data?.map { product ->
                            productList.add(ProductListViewItem.ItemProductListViewItem(product))
                        }
                        currentIndex = 0
                        loadNextPage()
                    }

                    is Resource.Error -> {
                        _uiState.value = ProductListUiState.Error(response.message)
                    }
                }
            }
        }
    }

    fun toggleFavorite(isFavorite:Boolean,id: String) {
        viewModelScope.launch {
            if (isFavorite) {
                removeFavoriteUseCase(FavoriteProduct(id)).collect{response->
                    when(response){
                        is Resource.Error -> {
                            _uiState.value = ProductListUiState.Error(response.message)
                        }
                        Resource.Loading -> {
                            _uiState.value = ProductListUiState.Loading
                        }
                        is Resource.Success<*> -> {
                            _uiState.value = ProductListUiState.RemoveFavorite
                        }
                    }
                }
            } else {
                addFavoriteProductUseCase(FavoriteProduct(id)).collect{response->
                    when(response){
                        is Resource.Error -> {
                            _uiState.value = ProductListUiState.Error(response.message)
                        }
                        Resource.Loading -> {
                            _uiState.value = ProductListUiState.Loading
                        }
                        is Resource.Success<*> -> {
                            _uiState.value = ProductListUiState.AddedFavorite
                        }
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        if (currentIndex >= productList.size) return

        val nextIndex = (currentIndex + pageSize).coerceAtMost(productList.size)
        val pageItems = productList.subList(currentIndex, nextIndex)
        currentIndex = nextIndex

        val currentList =
            (_uiState.value as? ProductListUiState.Success)?.productList?.toMutableList()
                ?: mutableListOf()

        currentList.addAll(pageItems)
        _uiState.value = ProductListUiState.Success(currentList)
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
            ).collect {response->
                when(response){
                    is Resource.Error -> {
                        _uiState.value = ProductListUiState.Error(response.message)
                    }
                    Resource.Loading -> {
                        _uiState.value = ProductListUiState.Loading
                    }
                    is Resource.Success<*> -> {
                        _uiState.value = ProductListUiState.AddedBasket
                    }
                }
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val filteredList = if(query.isEmpty()) productList else productList.filter { item ->
                when(item) {
                    is ProductListViewItem.ItemProductListViewItem -> {
                        item.product.name.contains(query, ignoreCase = true)
                    }
                    else -> false
                }
            }

            _uiState.value = ProductListUiState.Success(filteredList)
        }
    }
    fun filterAndSortProducts(brand: String?, model: String?, sortBy: SortBy?) {
        var filteredList = productList.filter { item ->
            if (item is ProductListViewItem.ItemProductListViewItem) {
                val matchesBrand = brand.isNullOrEmpty() || item.product.brand.equals(brand, ignoreCase = true)
                val matchesModel = model.isNullOrEmpty() || item.product.model.equals(model, ignoreCase = true)
                matchesBrand && matchesModel
            } else false
        }

        filteredList = when(sortBy) {
            SortBy.PRICE_ASC -> filteredList.sortedBy { (it as ProductListViewItem.ItemProductListViewItem).product.price.toDouble() }
            SortBy.PRICE_DESC -> filteredList.sortedByDescending { (it as ProductListViewItem.ItemProductListViewItem).product.price.toDouble() }
            SortBy.DATE_NEWEST -> filteredList.sortedByDescending { Instant.parse((it as ProductListViewItem.ItemProductListViewItem).product.createdAt) }
            SortBy.DATE_OLDEST -> filteredList.sortedBy { Instant.parse((it as ProductListViewItem.ItemProductListViewItem).product.createdAt) }
            null -> filteredList
        }

        _uiState.value = ProductListUiState.Success(filteredList)
    }
}

sealed interface ProductListUiState {
    data object Loading : ProductListUiState
    data class Success(val productList: List<ProductListViewItem>?) : ProductListUiState
    data object AddedFavorite:ProductListUiState
    data object RemoveFavorite:ProductListUiState
    data object AddedBasket:ProductListUiState
    data class Error(val message: String) : ProductListUiState
}

enum class SortBy { PRICE_ASC, PRICE_DESC, DATE_NEWEST, DATE_OLDEST }
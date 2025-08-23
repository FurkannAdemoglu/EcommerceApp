package com.example.ecommerceapp.presentation.ui.product.list

import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.base.BaseViewModel
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.product.AddFavoriteProductUseCase
import com.example.ecommerceapp.domain.usecase.product.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.GetProductsUseCase
import com.example.ecommerceapp.domain.usecase.product.RemoveFavoriteUseCase
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
    private val addToBasketProductUseCase: AddToBasketProductUseCase,
    private val getBasketProductUseCase: GetBasketProductUseCase
) : BaseViewModel(getBasketProductUseCase) {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    val fullProductList = mutableListOf<ProductListViewItem>()
    private var filteredProductList = mutableListOf<ProductListViewItem>()
    private val pageSize = 4
    private var currentIndex = 0

    init {
        getProductList()
    }

    private fun getProductList() {
        viewModelScope.launch(Dispatchers.IO) {
            getProductsUseCase.invoke().collect { response ->
                when (response) {
                    is Resource.Loading -> _uiState.value = ProductListUiState.Loading
                    is Resource.Success -> {
                        fullProductList.clear()
                        response.data?.map { product ->
                            fullProductList.add(ProductListViewItem.ItemProductListViewItem(product))
                        }
                        currentIndex = 0
                        // Filtre yoksa, tüm listeyi göster
                        filteredProductList = fullProductList.toMutableList()
                        loadNextPage(reset = true)
                    }
                    is Resource.Error -> _uiState.value = ProductListUiState.Error(response.message)
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

    fun loadNextPage(reset: Boolean = false) {
        if (reset) currentIndex = 0

        if (currentIndex >= filteredProductList.size) return

        val nextIndex = (currentIndex + pageSize).coerceAtMost(filteredProductList.size)
        val pageItems = filteredProductList.subList(currentIndex, nextIndex)
        currentIndex = nextIndex

        val currentList =
            if (reset) mutableListOf()
            else (_uiState.value as? ProductListUiState.Success)?.productList?.toMutableList() ?: mutableListOf()

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
                    is Resource.Success -> {
                        _uiState.value = ProductListUiState.AddedBasket
                        loadCartItemCount()
                    }
                }
            }
        }
    }

    fun filterProducts(
        selectedBrands: List<String> = emptyList(),
        selectedModels: List<String> = emptyList(),
        searchQuery: String = "",
        sortBy: SortBy? = null
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            filteredProductList = fullProductList.filter { item ->
                if (item is ProductListViewItem.ItemProductListViewItem) {
                    val matchesBrand = selectedBrands.isEmpty() || selectedBrands.contains(item.product.brand)
                    val matchesModel = selectedModels.isEmpty() || selectedModels.contains(item.product.model)
                    val matchesSearch = searchQuery.isEmpty() || (
                            item.product.name.contains(searchQuery, true) ||
                                    item.product.brand.contains(searchQuery, true) ||
                                    item.product.model.contains(searchQuery, true)
                            )
                    matchesBrand && matchesModel && matchesSearch
                } else false
            }.toMutableList()

            // Sıralama
            filteredProductList = when(sortBy) {
                SortBy.PRICE_ASC -> filteredProductList.sortedBy { (it as ProductListViewItem.ItemProductListViewItem).product.price.toDouble() }.toMutableList()
                SortBy.PRICE_DESC -> filteredProductList.sortedByDescending { (it as ProductListViewItem.ItemProductListViewItem).product.price.toDouble() }.toMutableList()
                SortBy.DATE_NEWEST -> filteredProductList.sortedByDescending { Instant.parse((it as ProductListViewItem.ItemProductListViewItem).product.createdAt) }.toMutableList()
                SortBy.DATE_OLDEST -> filteredProductList.sortedBy { Instant.parse((it as ProductListViewItem.ItemProductListViewItem).product.createdAt) }.toMutableList()
                null -> filteredProductList
            }

            currentIndex = 0
            loadNextPage(reset = true)
        }
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
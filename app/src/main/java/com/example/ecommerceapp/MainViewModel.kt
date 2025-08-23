package com.example.ecommerceapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.usecase.product.GetBasketProductUseCase
import com.example.ecommerceapp.utils.Resource
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBasketProductUseCase: GetBasketProductUseCase
) : ViewModel() {

    private val _bottomState = MutableLiveData<BottomNavigationAnnotation>()
    val bottomState: LiveData<BottomNavigationAnnotation> = _bottomState
    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    fun setBottomNavigationState(bottomNavigationAnnotation: BottomNavigationAnnotation) {
        _bottomState.value = bottomNavigationAnnotation
    }

    fun loadCartItemCount() {
        viewModelScope.launch {
            getBasketProductUseCase.invoke().collect { response ->
                when (response) {
                    is Resource.Error -> Unit
                    Resource.Loading -> Unit
                    is Resource.Success -> {
                        val totalCount = response.data?.size // quantity alanı varsa
                        _cartItemCount.value = totalCount ?: 0
                    }
                }
            }

        }
    }

}
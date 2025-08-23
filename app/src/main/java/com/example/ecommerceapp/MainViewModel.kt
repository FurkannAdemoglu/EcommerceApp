package com.example.ecommerceapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerceapp.base.BaseViewModel
import com.example.ecommerceapp.domain.usecase.product.GetBasketProductUseCase
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBasketProductUseCase: GetBasketProductUseCase
) : BaseViewModel(getBasketProductUseCase) {

    private val _bottomState = MutableLiveData<BottomNavigationAnnotation>()
    val bottomState: LiveData<BottomNavigationAnnotation> = _bottomState

    fun setBottomNavigationState(bottomNavigationAnnotation: BottomNavigationAnnotation) {
        _bottomState.value = bottomNavigationAnnotation
    }


}
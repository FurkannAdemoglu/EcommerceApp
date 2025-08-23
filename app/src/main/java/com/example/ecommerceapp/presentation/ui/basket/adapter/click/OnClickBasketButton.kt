package com.example.ecommerceapp.presentation.ui.basket.adapter.click

import com.example.ecommerceapp.domain.model.CartProduct

sealed interface OnClickBasketButton {
    class OnClickMinus(val cartProduct: CartProduct):OnClickBasketButton
    class OnClickPlus(val cartProduct: CartProduct):OnClickBasketButton
}
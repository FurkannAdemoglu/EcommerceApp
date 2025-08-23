package com.example.ecommerceapp.presentation.ui.product.list.adapter.click

import com.example.ecommerceapp.domain.model.Product

sealed interface OnClicksProduct {
    class OnClickRoot(val product: Product):OnClicksProduct
    class OnClickAddToCart(val product: Product):OnClicksProduct
    class OnClickAddToFavorite(val id:String,val isFavorite:Boolean,val position:Int):OnClicksProduct
}
package com.example.ecommerceapp.presentation.ui.basket.adapter.viewitem

import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.BaseViewItem
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.utils.EmptyView

sealed interface BasketListViewItem: BaseViewItem {
    class ItemProductBasketListViewItem(val cartProduct: CartProduct):BasketListViewItem{
        override val resource: Int
            get() = R.layout.item_basket_product
    }

    class ItemBasketEmptyViewItem(val emptyView: EmptyView):BasketListViewItem{
        override val resource: Int
            get() = R.layout.item_empty_view
    }
}
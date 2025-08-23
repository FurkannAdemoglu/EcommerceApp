package com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem

import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.BaseViewItem
import com.example.ecommerceapp.domain.model.Product

sealed interface ProductListViewItem:BaseViewItem {
    class ItemProductListViewItem(val product: Product):ProductListViewItem{
        override val resource: Int
            get() = R.layout.item_product_list

    }
}
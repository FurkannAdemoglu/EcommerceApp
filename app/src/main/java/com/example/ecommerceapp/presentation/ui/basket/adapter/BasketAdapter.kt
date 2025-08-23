package com.example.ecommerceapp.presentation.ui.basket.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.ItemEmptyViewHolder
import com.example.ecommerceapp.databinding.ItemBasketProductBinding
import com.example.ecommerceapp.databinding.ItemEmptyViewBinding
import com.example.ecommerceapp.presentation.ui.basket.adapter.click.OnClickBasketButton
import com.example.ecommerceapp.presentation.ui.basket.adapter.viewholder.ItemBasketProductViewHolder
import com.example.ecommerceapp.presentation.ui.basket.adapter.viewitem.BasketListViewItem
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.utils.viewBinding

class BasketAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val productListData = mutableListOf<BasketListViewItem>()
    lateinit var onClick:((onClickBasket:OnClickBasketButton)->Unit)
    lateinit var onClickEmptyButton:(()->Unit)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.item_basket_product->{
                ItemBasketProductViewHolder(parent.viewBinding(ItemBasketProductBinding::inflate),onClick)
            }
            R.layout.item_empty_view->{
                ItemEmptyViewHolder(parent.viewBinding(ItemEmptyViewBinding::inflate),onClickEmptyButton)
            }
            else-> throw IllegalArgumentException("Invalid view type provided")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val data = productListData[position]){
            is BasketListViewItem.ItemProductBasketListViewItem -> {
                (holder as ItemBasketProductViewHolder).bind(data.cartProduct)
            }

            is BasketListViewItem.ItemBasketEmptyViewItem -> {
                (holder as ItemEmptyViewHolder).bind(data.emptyView)
            }
        }
    }

    override fun getItemCount() = productListData.size

    override fun getItemViewType(position: Int): Int {
        return productListData[position].resource
    }

    fun setBasketListData(list:List<BasketListViewItem>){
        productListData.apply {
            clear()
            addAll(list)
            notifyDataSetChanged()
        }
    }

    fun updateItem(position:Int,isFavorite:Boolean){
        val item = productListData[position] as ProductListViewItem.ItemProductListViewItem
        item.product.isFavorite = !isFavorite
        notifyItemChanged(position)
    }
}
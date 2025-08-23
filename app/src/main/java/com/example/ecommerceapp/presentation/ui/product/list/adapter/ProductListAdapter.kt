package com.example.ecommerceapp.presentation.ui.product.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.ItemEmptyViewHolder
import com.example.ecommerceapp.databinding.ItemEmptyViewBinding
import com.example.ecommerceapp.databinding.ItemProductListBinding
import com.example.ecommerceapp.presentation.ui.product.list.adapter.click.OnClicksProduct
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewholder.ItemProductViewHolder
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.utils.viewBinding

class ProductListAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val productListData = mutableListOf<ProductListViewItem>()
    lateinit var onClick:((onClicksProduct: OnClicksProduct)->Unit)
    lateinit var onClickEmpty:(()->Unit)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.item_product_list->{
            ItemProductViewHolder(parent.viewBinding(ItemProductListBinding::inflate),onClick)
            }
            R.layout.item_empty_view->{
                ItemEmptyViewHolder(parent.viewBinding(ItemEmptyViewBinding::inflate),onClickEmpty)
            }
            else-> throw IllegalArgumentException("Invalid view type provided")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val data = productListData[position]){
            is ProductListViewItem.ItemProductListViewItem -> {
                (holder as ItemProductViewHolder).bind(data.product)
            }

            is ProductListViewItem.ItemBasketEmptyViewItem ->
                (holder as ItemEmptyViewHolder).bind(data.emptyView)
        }
    }

    override fun getItemCount() = productListData.size

    override fun getItemViewType(position: Int): Int {
        return productListData[position].resource
    }

    fun setProductListData(list:List<ProductListViewItem>){
        productListData.apply {
            clear()
            addAll(list)
            notifyDataSetChanged()
        }
    }
    fun removeItem(position: Int) {
        if (position in 0 until productListData.size) {
            productListData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, productListData.size)
        }
    }
    fun updateItem(position:Int,isFavorite:Boolean){
        val item = productListData[position] as ProductListViewItem.ItemProductListViewItem
        item.product.isFavorite = !isFavorite
        notifyItemChanged(position)
    }
}
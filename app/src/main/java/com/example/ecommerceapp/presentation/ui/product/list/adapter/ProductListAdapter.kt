package com.example.ecommerceapp.presentation.ui.product.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.ItemProductListBinding
import com.example.ecommerceapp.presentation.ui.product.list.adapter.click.OnClicksProduct
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewholder.ItemProductViewHolder
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.utils.viewBinding

class ProductListAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val productListData = mutableListOf<ProductListViewItem>()
    lateinit var onClick:((onClicksProduct: OnClicksProduct)->Unit)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.item_product_list->{
            ItemProductViewHolder(parent.viewBinding(ItemProductListBinding::inflate),onClick)
            }
            else-> throw IllegalArgumentException("Invalid view type provided")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val data = productListData[position]){
            is ProductListViewItem.ItemProductListViewItem -> {
                (holder as ItemProductViewHolder).bind(data.product)
            }
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

    fun updateItem(position:Int,isFavorite:Boolean){
        val item = productListData[position] as ProductListViewItem.ItemProductListViewItem
        item.product.isFavorite = !isFavorite
        notifyItemChanged(position)
    }
}
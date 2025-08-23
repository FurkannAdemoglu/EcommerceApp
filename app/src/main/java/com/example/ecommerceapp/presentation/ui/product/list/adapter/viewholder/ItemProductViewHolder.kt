package com.example.ecommerceapp.presentation.ui.product.list.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.databinding.ItemProductListBinding
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.presentation.ui.product.list.adapter.click.OnClicksProduct

class ItemProductViewHolder(
    private val binding:ItemProductListBinding,
    private val onClick:((onClicksProduct:OnClicksProduct)->Unit)
): RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product){
        binding.product = product
        binding.root.setOnClickListener {
            onClick.invoke(OnClicksProduct.OnClickRoot(product))
        }
        binding.imgFavorite.setOnClickListener {
            onClick.invoke(OnClicksProduct.OnClickAddToFavorite(product.id,product.isFavorite,bindingAdapterPosition))
        }
        binding.txtAddToCart.setOnClickListener {
            onClick.invoke(OnClicksProduct.OnClickAddToCart(product))
        }
    }
}
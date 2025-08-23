package com.example.ecommerceapp.presentation.ui.basket.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.databinding.ItemBasketProductBinding
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.presentation.ui.basket.adapter.click.OnClickBasketButton

class ItemBasketProductViewHolder (
    private val binding: ItemBasketProductBinding,
    private val onClick:((onClickBasket: OnClickBasketButton)->Unit)
): RecyclerView.ViewHolder(binding.root) {
    fun bind(cartProduct: CartProduct){
        binding.cartProduct = cartProduct
        binding.txtMinus.setOnClickListener {
            onClick.invoke(OnClickBasketButton.OnClickMinus(cartProduct))
        }
        binding.txtPlus.setOnClickListener {
            onClick.invoke(OnClickBasketButton.OnClickPlus(cartProduct))
        }
    }
}
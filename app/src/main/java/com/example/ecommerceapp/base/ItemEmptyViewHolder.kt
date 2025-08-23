package com.example.ecommerceapp.base

import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.databinding.ItemEmptyViewBinding
import com.example.ecommerceapp.utils.EmptyView

class ItemEmptyViewHolder (
    private val binding: ItemEmptyViewBinding,
    private val onClick:(()->Unit)
): RecyclerView.ViewHolder(binding.root) {
    fun bind(emptyView: EmptyView){
        binding.emptyViewData = emptyView
        binding.txtGoHomePage.setOnClickListener {
            onClick.invoke()
        }
    }
}
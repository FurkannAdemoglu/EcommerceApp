package com.example.ecommerceapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecommerceapp.R

@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .error(R.color.gray_ec)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}
package com.example.ecommerceapp.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecommerceapp.R
import java.text.NumberFormat
import java.util.Locale

@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .error(R.color.gray_ec)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

@BindingAdapter("isVisible")
fun View.setIsVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("priceWithCurrency")
fun setPriceWithCurrency(textView: AppCompatTextView, price: String?) {
    price?.toDoubleOrNull()?.let { number ->
        val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
        textView.text = format.format(number)
    } ?: run {
        textView.text = ""
    }
}
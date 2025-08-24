package com.example.ecommerceapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.example.ecommerceapp.domain.model.CartProduct
import java.text.NumberFormat
import java.util.Locale

fun List<CartProduct>.totalPriceFormatted(): String {
    val total = this.sumOf { product ->
        val priceDouble = product.price.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        priceDouble * product.quantity
    }
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(total)
}

fun Context.openToast(message:String,toastLength:Int){
    Toast.makeText(this,message,toastLength).show()
}

fun Context.isConnected():Boolean{
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
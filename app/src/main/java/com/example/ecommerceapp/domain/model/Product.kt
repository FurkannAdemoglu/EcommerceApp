package com.example.ecommerceapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val name:String,
    val image:String,
    val price:String,
    val description:String,
    val model:String,
    val brand:String,
    val id:String,
    val createdAt:String,
    var isFavorite: Boolean = false
):Parcelable

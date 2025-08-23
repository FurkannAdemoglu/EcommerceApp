package com.example.ecommerceapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_products")
data class CartProduct(
    @PrimaryKey val id:String,
    val name:String,
    val price:String,
    val quantity:Int=1
){
    fun getProductTotalPrice():String{
        return price.toDouble().times(quantity).toString()
    }
    fun getQuantityString():String{
        return quantity.toString()
    }
}

package com.example.ecommerceapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecommerceapp.domain.model.CartProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cartProduct: CartProduct)

    @Update
    suspend fun updateCart(cartProduct: CartProduct)

    @Delete
    suspend fun deleteCart(cartProduct: CartProduct)

    @Query("SELECT * FROM cart_products WHERE id = :id LIMIT 1")
    suspend fun getCartProductById(id: String): CartProduct?

    @Query("UPDATE cart_products SET quantity = quantity - 1 WHERE id = :id")
    suspend fun decreaseQuantity(id: String)

    @Query("SELECT * FROM cart_products")
    fun getAllCartProduct(): Flow<List<CartProduct>>

    @Delete
    suspend fun deleteAll(items:List<CartProduct>)


}
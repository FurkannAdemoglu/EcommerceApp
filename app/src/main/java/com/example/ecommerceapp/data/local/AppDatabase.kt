package com.example.ecommerceapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct

@Database(
    entities = [FavoriteProduct::class,CartProduct::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    abstract fun cartDao(): CartDao
}

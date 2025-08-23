package com.example.ecommerceapp.di

import com.example.ecommerceapp.data.datasource.local.cart.CartLocalDataSource
import com.example.ecommerceapp.data.datasource.local.cart.CartLocalDataSourceImpl
import com.example.ecommerceapp.data.datasource.local.favorite.FavoriteLocalDataSource
import com.example.ecommerceapp.data.datasource.local.favorite.FavoriteLocalDataSourceImpl
import com.example.ecommerceapp.data.datasource.remote.ProductRemoteDataSource
import com.example.ecommerceapp.data.datasource.remote.ProductRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFlightRemoteDataSource(
        impl: ProductRemoteDataSourceImpl
    ): ProductRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFavoriteLocalDataSource(
        impl: FavoriteLocalDataSourceImpl
    ): FavoriteLocalDataSource

    @Binds
    @Singleton
    abstract fun bindCartLocalDataSource(
        impl: CartLocalDataSourceImpl
    ): CartLocalDataSource
}
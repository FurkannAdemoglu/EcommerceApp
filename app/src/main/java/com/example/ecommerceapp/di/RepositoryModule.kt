package com.example.ecommerceapp.di

import com.example.ecommerceapp.data.datasource.local.cart.CartLocalDataSourceImpl
import com.example.ecommerceapp.data.datasource.local.favorite.FavoriteLocalDataSourceImpl
import com.example.ecommerceapp.data.datasource.remote.ProductRemoteDataSourceImpl
import com.example.ecommerceapp.data.repository.CartRepositoryImpl
import com.example.ecommerceapp.data.repository.FavoriteRepositoryImpl
import com.example.ecommerceapp.data.repository.ProductRepositoryImpl
import com.example.ecommerceapp.domain.repository.CartRepository
import com.example.ecommerceapp.domain.repository.FavoriteRepository
import com.example.ecommerceapp.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideFlightRepository(
        remoteDataSource: ProductRemoteDataSourceImpl
    ): ProductRepository {
        return ProductRepositoryImpl(remoteDataSource)
    }


    @Provides
    @Singleton
    fun provideFavoriteRepository(
        remoteDataSource: FavoriteLocalDataSourceImpl
    ): FavoriteRepository {
        return FavoriteRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        remoteDataSource: CartLocalDataSourceImpl
    ): CartRepository {
        return CartRepositoryImpl(remoteDataSource)
    }
}
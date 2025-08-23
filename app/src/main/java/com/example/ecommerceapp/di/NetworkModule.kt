package com.example.ecommerceapp.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.example.ecommerceapp.BuildConfig
import com.example.ecommerceapp.data.remote.api.ProductApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()

    @Provides
    @Singleton
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory =
        GsonConverterFactory.create(GsonBuilder().create())


    @Provides
    @Singleton
    fun provideChucker(@ApplicationContext application: Context): ChuckerInterceptor {
        val chuckerCollector = ChuckerCollector(
            context = application,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
        return ChuckerInterceptor.Builder(application)
            .collector(chuckerCollector)
            .maxContentLength(550_000L)
            .alwaysReadResponseBody(true)
            .build()
    }


    @Provides
    @Singleton
    fun provideApi(
        builder: Retrofit.Builder,
        okHttpClientBuilder: OkHttpClient.Builder,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        converterFactory: Converter.Factory,
        chuckerInterceptor: ChuckerInterceptor
    ): ProductApiService {

        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addNetworkInterceptor(httpLoggingInterceptor)
        okHttpClientBuilder.addNetworkInterceptor(chuckerInterceptor)

        val client = okHttpClientBuilder.build()
        return builder.client(client)
            .baseUrl("https://5fc9346b2af77700165ae514.mockapi.io/")
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(ProductApiService::class.java)
    }
}
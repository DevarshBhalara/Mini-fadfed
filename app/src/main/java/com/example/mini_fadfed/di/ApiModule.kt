package com.example.mini_fadfed.di

import android.content.Context
import com.example.mini_fadfed.data.repository.RegisterUserRepository
import com.example.mini_fadfed.data.service.ApiService
import com.example.mini_fadfed.interceptor.AuthInterceptor
import com.example.mini_fadfed.utils.AppConstants
import com.example.mini_fadfed.utils.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideApiRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(client) // Use OkHttpClient with authentication
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val devid = Utils.getDeviceId(context)
        val sessionId = Utils.generateSessionId()

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(devid, sessionId))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService
        = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRegisterUserRepository(apiService: ApiService): RegisterUserRepository {
        return RegisterUserRepository(apiService)
    }
}
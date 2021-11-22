package com.basil.taxiprepaid.di

import com.basil.taxiprepaid.BuildConfig
import com.basil.taxiprepaid.data.remote.ApiService
import com.basil.taxiprepaid.data.remote.JSONHeaderInterceptor
import com.basil.taxiprepaid.data.remote.MapService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.roshnee.data.repository.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import java.util.*


@InstallIn(SingletonComponent::class)
@Module()
open class NetworkModule {

    @Provides
    fun provideOkHttpClient(preferenceRepository: PreferenceRepository): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor(JSONHeaderInterceptor(preferenceRepository))
        .connectTimeout(1, TimeUnit.DAYS)
        .writeTimeout(1, TimeUnit.DAYS)
        .readTimeout(1, TimeUnit.DAYS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(interceptor)
        } else {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.NONE
            client.addInterceptor(interceptor)
        }
        return client.build()
    }


    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .registerTypeAdapter(Date::class.java, DateSerializer())
        .serializeNulls()
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.END_POINT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMapService(gson: Gson): MapService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/distancematrix/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(MapService::class.java)
    }
}
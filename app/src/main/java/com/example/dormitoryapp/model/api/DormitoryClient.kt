package com.example.dormitoryapp.model.api

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DormitoryClient {
    private val interceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
    private val client = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        .readTimeout(40, TimeUnit.SECONDS)
        .connectTimeout(40, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .retryOnConnectionFailure(true)
        .build()

    private val baseUrl = "http://192.168.2.5:45499/api/"
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(client)
        .build()
        .create(DormitoryService::class.java)
}
package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.BuildConfig
import com.cornershop.counterstest.data.remote.Api
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class ApiModule {

    @Provides
    fun provideHttpInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun providesRetrofit(interceptor: HttpLoggingInterceptor): Retrofit = run {
        val httpClient = createHttpClient(interceptor)
        Retrofit
            .Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.URL_API)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createHttpClient(httpInterceptor: Interceptor) =
        OkHttpClient.Builder()
            .addInterceptor(httpInterceptor)
            .callTimeout(
                3,
                TimeUnit.MINUTES
            )
            .readTimeout(
                3,
                TimeUnit.MINUTES
            )
            .connectTimeout(
                3,
                TimeUnit.MINUTES
            )
            .build()


    @Provides
    fun providesApiService(retrofit: Retrofit) = retrofit.create(Api::class.java)
}
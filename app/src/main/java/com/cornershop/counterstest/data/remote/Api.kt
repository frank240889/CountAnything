package com.cornershop.counterstest.data.remote

import com.cornershop.counterstest.domain.remote.Counter
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.domain.remote.CounterName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {
    @GET("/api/v1/counters")
    @Headers("Content-Type: application/json")
    suspend fun counters(): List<Counter>

    @POST("/api/v1/counter")
    @Headers("Content-Type: application/json")
    suspend fun create(@Body counterName: CounterName): List<Counter>

    @POST("/api/v1/counter/inc")
    @Headers("Content-Type: application/json")
    suspend fun increment(@Body counterId: CounterId): List<Counter>

    @POST("/api/v1/counter/dec")
    @Headers("Content-Type: application/json")
    suspend fun decrement(@Body counterId: CounterId): List<Counter>
}
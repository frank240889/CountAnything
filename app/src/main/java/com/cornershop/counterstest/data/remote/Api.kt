package com.cornershop.counterstest.data.remote

import com.cornershop.counterstest.domain.remote.Counter
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.domain.remote.CounterName
import retrofit2.http.*

/**
 * The point to make the request. Its implementation is managed by Retrofit.
 */
interface Api {
    @GET("/api/v1/counters")
    @Headers("Content-Type: application/json")
    suspend fun getAllRemoteCounters(): List<Counter>

    @POST("/api/v1/counter")
    @Headers("Content-Type: application/json")
    suspend fun createRemoteCounter(@Body counterName: CounterName): List<Counter>

    @POST("/api/v1/counter/inc")
    @Headers("Content-Type: application/json")
    suspend fun increment(@Body counterId: CounterId): List<Counter>

    @POST("/api/v1/counter/dec")
    @Headers("Content-Type: application/json")
    suspend fun decrement(@Body counterId: CounterId): List<Counter>

    @HTTP(method = "DELETE", path = "/api/v1/counter", hasBody = true)
    @Headers("Content-Type: application/json")
    suspend fun deleteRemoteCounter(@Body counterId: CounterId): List<Counter>
}
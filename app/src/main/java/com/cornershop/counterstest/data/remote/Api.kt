package com.cornershop.counterstest.data.remote

import com.cornershop.counterstest.domain.Counter
import retrofit2.http.GET

interface Api {
    @GET("/api/v1/counters")
    suspend fun counters(): List<Counter>
}
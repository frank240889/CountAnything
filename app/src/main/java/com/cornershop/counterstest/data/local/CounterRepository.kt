package com.cornershop.counterstest.data.local

import com.cornershop.counterstest.data.remote.Api
import javax.inject.Inject

class CounterRepository @Inject constructor(
    private val api: Api
) {
    suspend fun counters() = api.counters()
}
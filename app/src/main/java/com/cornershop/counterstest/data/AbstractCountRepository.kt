package com.cornershop.counterstest.data

import androidx.lifecycle.LiveData
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.remote.CounterName

interface AbstractCountRepository {
    suspend fun fetchCounters(forceRemoteFetching: Boolean = false)

    suspend fun createLocalCounter(counterName: CounterName)

    suspend fun incrementCounterByOne(counterEntity: CounterEntity)

    suspend fun decrementCounterByOne(counterEntity: CounterEntity)

    suspend fun deleteCounter(counter: CounterEntity)

    fun localCountersObservable(): LiveData<List<CounterEntity>>
}
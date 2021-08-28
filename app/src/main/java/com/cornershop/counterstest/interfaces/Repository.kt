package com.cornershop.counterstest.interfaces

import androidx.lifecycle.LiveData
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.remote.CounterName

/**
 * A repository to fetch data.
 */
interface Repository {
    suspend fun fetchCounters(forceRemoteFetching: Boolean = false): Int

    suspend fun createLocalCounter(counterName: CounterName)

    suspend fun incrementCounterByOne(counterEntity: CounterEntity)

    suspend fun decrementCounterByOne(counterEntity: CounterEntity)

    suspend fun deleteCounter(counter: CounterEntity)

    /**
     * Returns a observable to stay pending when new updates are made.
     */
    fun localCountersObservable(): LiveData<List<CounterEntity>>
}
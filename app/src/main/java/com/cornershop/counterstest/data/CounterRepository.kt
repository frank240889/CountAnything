package com.cornershop.counterstest.data

import com.cornershop.counterstest.data.local.database.CountersDatabase
import com.cornershop.counterstest.data.remote.Api
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.remote.Counter
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.domain.remote.CounterName
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterRepository @Inject constructor(
    private val api: Api,
    private val db: CountersDatabase
) {

    suspend fun fetchCounters(forceRemoteFetching: Boolean = false) {
        clearAll()
        if (forceRemoteFetching || localCount() == 0) {
            fetchRemoteCounters().apply {
                insertCounters(this)
            }
        }
    }

    suspend fun createLocalCounter(counterName: CounterName) {
        api.createRemoteCounter(counterName).apply {
            insertCounters(this)
        }
    }

    suspend fun incrementCounterByOne(counterEntity: CounterEntity) {
        api.increment(CounterId(counterEntity.id))
        db.countersDao().update(counterEntity.copy(count = counterEntity.count + 1))
    }

    suspend fun decrementCounterByOne(counterEntity: CounterEntity) {
        api.decrement(CounterId(counterEntity.id))
        db.countersDao().update(counterEntity.copy(count = counterEntity.count - 1))
    }

    suspend fun deleteCounter(counter: CounterEntity) {
        api.deleteRemoteCounter(CounterId(counter.id)).let {
            db.countersDao().delete(counter)
        }
    }

    fun localCountersObservable() = db.countersDao().counters()

    private fun insertCounters(remoteCounters: List<Counter>) {
        db.countersDao().insert(remoteCounters.map { counter ->
            CounterEntity(
                counter.id,
                counter.title,
                counter.count
            )
        })
    }

    private suspend fun localCount() = db.countersDao().count()

    private suspend fun fetchRemoteCounters() = api.getAllRemoteCounters()

    private fun clearAll() = db.clearAllTables()
}
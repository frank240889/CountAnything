package com.cornershop.counterstest.data

import com.cornershop.counterstest.data.local.database.CountersDao
import com.cornershop.counterstest.data.remote.Api
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.remote.Counter
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.domain.remote.CounterName
import com.cornershop.counterstest.interfaces.Repository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The repository from which the app takes de data. Is the implementation for [Repository]
 */
@Singleton
class CounterRepository @Inject constructor(
    private val api: Api,
    private val dao: CountersDao
): Repository {

    override suspend fun fetchCounters(forceRemoteFetching: Boolean): Int {
        var dataSize = 0
        clearAll()
        if (forceRemoteFetching || localCount() == 0) {
            fetchRemoteCounters().apply {
                dataSize = this.size
                insertCounters(this)
            }
        }
        return dataSize
    }

    override suspend fun createLocalCounter(counterName: CounterName) {
        api.createRemoteCounter(counterName).apply {
            insertCounters(this)
        }
    }

    override suspend fun incrementCounterByOne(counterEntity: CounterEntity) {
        api.increment(CounterId(counterEntity.id))
        dao.update(counterEntity.copy(count = counterEntity.count + 1))
    }

    override suspend fun decrementCounterByOne(counterEntity: CounterEntity) {
        api.decrement(CounterId(counterEntity.id))
        dao.update(counterEntity.copy(count = counterEntity.count - 1))
    }

    override suspend fun deleteCounter(counter: CounterEntity) {
        api.deleteRemoteCounter(CounterId(counter.id)).let {
            dao.delete(counter)
        }
    }

    override fun localCountersObservable() = dao.counters()

    private fun insertCounters(remoteCounters: List<Counter>) {
        dao.insert(remoteCounters.map { counter ->
            CounterEntity(
                counter.id,
                counter.title,
                counter.count
            )
        })
    }

    private suspend fun localCount() = dao.count()

    private suspend fun fetchRemoteCounters() = api.getAllRemoteCounters()

    private fun clearAll() = dao.clear()
}
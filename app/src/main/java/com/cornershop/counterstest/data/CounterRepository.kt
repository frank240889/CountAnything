package com.cornershop.counterstest.data

import com.cornershop.counterstest.data.local.CountersDatabase
import com.cornershop.counterstest.data.remote.Api
import com.cornershop.counterstest.domain.local.CounterEntity
import com.cornershop.counterstest.domain.remote.Counter
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.domain.remote.CounterName
import javax.inject.Inject
import kotlin.math.abs

class CounterRepository @Inject constructor(
    private val api: Api,
    private val db: CountersDatabase
) {

    fun localCountersObservable() = db.countersDao().getLocalCountersObservable()

    suspend fun fetchCounters() = db.countersDao().count().let { localCounters ->
        if (localCounters == 0) {
            insertCounters(api.getAllRemoteCounters())
        }
    }

    suspend fun createLocalCounter(counterName: CounterName) {
        insertCounters(api.createRemoteCounter(counterName))
    }

    suspend fun localCount() = db.countersDao().count()

    suspend fun incrementCounterByOne(counterEntity: CounterEntity) =
        updateLocalCounter(counterEntity.copy(count = counterEntity.count + 1, pendingUpdate = true)).let { res ->
            if (res == 1) {
                incrementRemoteValue(1, counterEntity.id)
                updateLocalCounter(counterEntity.copy(count = counterEntity.count + 1, pendingUpdate = false))
            }
        }

    suspend fun decrementCounterByOne(counterEntity: CounterEntity) =
        updateLocalCounter(counterEntity.copy(count = counterEntity.count - 1, pendingUpdate = true)).let { res ->
            if (res == 1) {
                decrementRemoteCounter(1, counterEntity.id)
                updateLocalCounter(counterEntity.copy(count = counterEntity.count - 1, pendingUpdate = false))
            }
        }

    suspend fun searchCounter(title: String) = db.countersDao().searchCounter("%$title%")

    suspend fun deleteCounter(counter: CounterEntity) {
        api.deleteRemoteCounter(CounterId(counter.id)).let {
            db.countersDao().delete(counter)
        }
    }

    suspend fun syncLocalCounters(): Boolean {
        var sync = false
        val remoteCounters = api.getAllRemoteCounters()
        db.countersDao().getCountersPendingToUpdate().forEach { localCounter ->
            remoteCounters.find { remoteCounter ->
                remoteCounter.id == localCounter.id
            }?.let { remoteCounter ->
                sync = updateCounterIfNeeded(localCounter, remoteCounter)
            }
        }

        return sync
    }

    suspend fun getCountersPendingToUpdate() = db.countersDao().getCountersPendingToUpdate()

    private suspend fun updateCounterIfNeeded(localCounter: CounterEntity, remoteCounter: Counter): Boolean {
        val diff = abs(localCounter.count - remoteCounter.count)
        var synced = false
        synced = if (localCounter.count < remoteCounter.count ) {
            decrementRemoteCounter(diff, localCounter.id).apply {
                updateLocalCounter(localCounter.copy(pendingUpdate = false))
            }
        } else {
            incrementRemoteValue(diff, localCounter.id).apply {
                updateLocalCounter(localCounter.copy(pendingUpdate = false))
            }
        }
        return synced
    }

    private suspend fun incrementRemoteValue(diff: Int, id: String): Boolean {
        for (i in 0 until diff - 1) {
            api.increment(CounterId(id))
        }
        return true
    }

    private suspend fun decrementRemoteCounter(diff: Int, id: String): Boolean {
        for (i in diff -1 downTo 0) {
            api.decrement(CounterId(id))
        }
        return true
    }

    private suspend fun insertCounters(remoteCounters: List<Counter>) {
        db.countersDao().insertCounters(remoteCounters.map { counter ->
            CounterEntity(
                counter.id,
                counter.title,
                counter.count
            )
        })
    }

    private fun updateLocalCounter(it: CounterEntity): Int = db.countersDao().updateCounter(it)
}
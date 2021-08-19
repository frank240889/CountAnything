package com.cornershop.counterstest.data

import com.cornershop.counterstest.common.toCounterEntity
import com.cornershop.counterstest.data.local.CountersDatabase
import com.cornershop.counterstest.data.remote.Api
import com.cornershop.counterstest.domain.local.CounterEntity
import com.cornershop.counterstest.domain.remote.Counter
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.domain.remote.CounterName
import javax.inject.Inject

class CounterRepository @Inject constructor(
    private val api: Api,
    private val db: CountersDatabase
) {
    suspend fun fetchCounters() = api.counters().let { counters ->
        db.countersDao().insertCounters(counters.map { counter ->
            CounterEntity(
                counter.id,
                counter.title,
                counter.count
            )
        })
    }

    suspend fun createCounter(counterName: CounterName) {
        api.create(counterName).let { counters ->
            db.countersDao().insertCounters(counters.map { counter ->
                CounterEntity(
                    counter.id,
                    counter.title,
                    counter.count
                )
            })
        }
    }

    suspend fun count() = db.countersDao().count()

    fun counters() = db.countersDao().getCounters()

    suspend fun increment(counterId: CounterId) = findAndUpdate(api.increment(counterId), counterId.id)

    suspend fun decrement(counterId: CounterId) = findAndUpdate(api.decrement(counterId), counterId.id)

    private fun findAndUpdate(counters: List<Counter>, id: String) {
        counters
            .find {
                it.id == id
            }?.toCounterEntity()?.let {
                db.countersDao().updateCounter(it)
            }
    }
}
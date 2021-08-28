package com.cornershop.counterstest.data

import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.remote.CounterName
import com.cornershop.counterstest.interfaces.Repository
import java.util.*
import kotlin.collections.ArrayList

@CustomAnnotations.OpenForTesting
class FakeRepository: Repository {

    private val fakeData = MutableLiveData<List<CounterEntity>>(emptyList())

    override suspend fun fetchCounters(forceRemoteFetching: Boolean): Int {
        var dataSize= 0
        fakeData.value = returnNewList(fakeData.value!!).apply {
            dataSize = this.size
        }
        return dataSize
    }

    override suspend fun createLocalCounter(counterName: CounterName) {
        createCounter(counterName.title).apply {
            create(this)
        }
    }

    override suspend fun incrementCounterByOne(counterEntity: CounterEntity) {
        inc(counterEntity)
    }

    override suspend fun decrementCounterByOne(counterEntity: CounterEntity) {
        dec(counterEntity)
    }

    override suspend fun deleteCounter(counter: CounterEntity) {
        delete(counter)
    }

    override fun localCountersObservable() = fakeData

    private fun createCounter(name: String) = CounterEntity(
        UUID.randomUUID().toString().substring(0, 6),
        name,
        0
    )

    private fun create(counter: CounterEntity) {
        fakeData.value = ArrayList(fakeData.value!!).apply {
            add(counter)
        }
    }
    private fun delete(counter: CounterEntity) = find(counter).let {
        val data = dataSource()
        val newData = ArrayList(data)
        newData.removeAt(0)
        fakeData.value = newData
    }

    private fun inc(counter: CounterEntity) = find(counter)?.let {
        counter.count = counter.count.inc()
        val newCounter = CounterEntity(counter.id, counter.title, counter.count)
        val newData = ArrayList(dataSource()).apply {
            set(indexOfFirst { it.id == counter.id }, newCounter)
        }
        fakeData.value = newData
    }

    private fun dec(counter: CounterEntity) = find(counter)?.let {
        counter.count = counter.count.dec()
        val newCounter = CounterEntity(counter.id, counter.title, counter.count)
        val newData = ArrayList(dataSource()).apply {
            set(indexOfFirst { it.id == counter.id }, newCounter)
        }
        fakeData.value = newData
    }

    private fun find(counter: CounterEntity) = fakeData.value
        ?.find {
            it.id == counter.id
        }

    private fun returnNewList(origin: List<CounterEntity>) = ArrayList<CounterEntity>().apply {
        addAll(origin)
    }.toList()

    private fun dataSource() = fakeData.value!!
}
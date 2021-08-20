package com.cornershop.counterstest.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.domain.local.CounterEntity
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.usecase.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getCounters: GetCounters,
    private val incrementCounter: IncrementCounter,
    private val decrementCounter: DecrementCounter,
    private val searchCounter: SearchCounter,
    private val deleteCounter: DeleteCounter
): ViewModel() {

    private val _counters: MediatorLiveData<State<List<CounterEntity>>> by lazy {
        MediatorLiveData()
    }

    val counters: LiveData<State<List<CounterEntity>>> get() = _counters

    init {
        _counters.addSource(getCounters.counters) {
            _counters.value = it
        }
    }

    fun performAction(action: CounterAdapter.Companion.Action, counter: CounterEntity) {
        if(action == CounterAdapter.Companion.Action.INCREMENT) {
            incrementCounter(counter)
        }
        else if (action == CounterAdapter.Companion.Action.DECREMENT) {
            decrementCounter(counter)
        }
        else{
            addForDeletion(counter)
        }
    }

    fun observeCounters() = getCounters.counters

    fun observeDeletionCounters() = deleteCounter.response

    fun getCounters() = getCounters.execute()

    fun onActiveSearch() {
        _counters.removeSource(getCounters.counters)
        _counters.addSource(searchCounter.searchResults) {
            _counters.value = it
        }
    }

    fun onInactiveActiveSearch() {
        _counters.removeSource(searchCounter.searchResults)
        _counters.addSource(getCounters.counters) {
            _counters.value = it
        }
    }

    fun search(title: String) {
        searchCounter.title = title
        searchCounter.execute()
    }

    fun delete() = deleteCounter.execute()

    fun clearDeletionList() = deleteCounter.clearDeletionList()

    private fun incrementCounter(counter: CounterEntity) {
        CounterId(counter.id).let { counterId ->
            incrementCounter.counterId = counterId
            incrementCounter.execute()
        }
    }

    private fun decrementCounter(counter: CounterEntity) {
        if (counter.count == 0) return

        CounterId(counter.id).let { counterId ->
            decrementCounter.counterId = counterId
            decrementCounter.execute()
        }
    }

    private fun addForDeletion(counter: CounterEntity) {
        deleteCounter.addToDelete(counter)
    }

    fun getCountersName() = deleteCounter.getCountersName()

}
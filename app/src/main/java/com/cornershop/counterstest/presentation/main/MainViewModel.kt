package com.cornershop.counterstest.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.domain.local.CounterEntity
import com.cornershop.counterstest.usecase.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getCounters: GetCounters,
    private val incrementCounter: IncrementCounter,
    private val decrementCounter: DecrementCounter,
    private val searchCounter: SearchCounter,
    private val deleteCounter: DeleteCounter,
    private val syncCounters: SyncCounters
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

    override fun onCleared() {
        releaseUseCases()
    }

    fun observeDeletionCounters() = deleteCounter.response

    fun observeSyncCounters() = syncCounters.response

    fun fetchCounters() = getCounters.execute()

    fun performAction(action: CounterAdapter.Companion.Action, counter: CounterEntity) {
        when (action) {
            CounterAdapter.Companion.Action.INCREMENT -> {
                incrementCounter(counter)
            }
            CounterAdapter.Companion.Action.DECREMENT -> {
                decrementCounter(counter)
            }
            else -> {
                addForDeletion(counter)
            }
        }
    }

    fun delete() = deleteCounter.execute()

    fun clearDeletionList() = deleteCounter.clearDeletionList()

    fun getCountersName() = deleteCounter.getCountersName()

    fun syncCounters() = syncCounters.execute()

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


    private fun incrementCounter(counter: CounterEntity) {
        incrementCounter.counterEntity = counter
        incrementCounter.execute()
    }

    private fun decrementCounter(counter: CounterEntity) {
        if (counter.count == 0) return

        decrementCounter.counterEntity = counter
        decrementCounter.execute()
    }

    private fun addForDeletion(counter: CounterEntity) {
        deleteCounter.addToDelete(counter)
    }

    private fun releaseUseCases() {
        getCounters.release()
        incrementCounter.release()
        decrementCounter.release()
        searchCounter.release()
        deleteCounter.release()
    }
}
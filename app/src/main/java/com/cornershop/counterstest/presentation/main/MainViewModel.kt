package com.cornershop.counterstest.presentation.main

import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.data.local.cache.Cache
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.local.usecase.*
import com.cornershop.counterstest.presentation.common.CounterAdapter
import javax.inject.Inject

open class MainViewModel @Inject constructor(
    private val getCounters: GetCounters,
    private val incrementCounter: IncrementCounter,
    private val decrementCounter: DecrementCounter,
    private val deleteCounter: DeleteCounter,
    private val shareCounter: ShareCounter,
    private val cache: Cache<CounterEntity, List<CounterEntity>>
): ViewModel() {


    override fun onCleared() {
        releaseUseCases()
        cache.clear()
    }

    fun observeCounters() = getCounters.counters

    fun observeDeletionCounters() = deleteCounter.response

    fun observeShareCounter() = shareCounter.response

    fun currentIncrementCounter() = incrementCounter.counterEntity

    fun currentDecrementCounter() = decrementCounter.counterEntity

    fun fetchCounters(forceRefresh: Boolean = false) {
        getCounters.apply {
            this.forceRefresh = forceRefresh
            execute()
        }
    }

    fun performAction(action: CounterAdapter.Companion.Action, counter: CounterEntity) {
        when (action) {
            CounterAdapter.Companion.Action.INCREMENT -> {
                incrementCounter(counter)
            }
            CounterAdapter.Companion.Action.DECREMENT -> {
                decrementCounter(counter)
            }
            else -> {
                handleMultiselect(counter)
            }
        }
    }

    fun delete() = deleteCounter.execute()

    fun share() = shareCounter.execute()

    fun clearMultiselect() {
        cache.clear()
    }

    fun getCountersName() = deleteCounter.getCountersName()

    fun observeIncrementCounter() = incrementCounter.response

    fun observeDecrementCounter() = decrementCounter.response

    fun countersCanBeDeleted() = cache.data().isNotEmpty()

    fun countersCanBeShared() = cache.data().isNotEmpty()

    fun counterAmount() = cache.data().size

    private fun incrementCounter(counter: CounterEntity) {
        incrementCounter.counterEntity = counter
        incrementCounter.execute()
    }

    private fun decrementCounter(counter: CounterEntity) {
        if (counter.count == 0) return

        decrementCounter.counterEntity = counter
        decrementCounter.execute()
    }

    private fun handleMultiselect(counter: CounterEntity) {
        if (cache.contains(counter))
            cache.remove(counter)
        else
            cache.add(counter)
    }

    private fun releaseUseCases() {
        getCounters.release()
        incrementCounter.release()
        decrementCounter.release()
        deleteCounter.release()
    }
}
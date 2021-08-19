package com.cornershop.counterstest.presentation.main

import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.domain.local.CounterEntity
import com.cornershop.counterstest.domain.remote.CounterId
import com.cornershop.counterstest.usecase.Counters
import com.cornershop.counterstest.usecase.DecrementCounter
import com.cornershop.counterstest.usecase.IncrementCounter
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val counters: Counters,
    private val incrementCounter: IncrementCounter,
    private val decrementCounter: DecrementCounter
): ViewModel() {
    fun performAction(action: CounterAdapter.Companion.Action, counter: CounterEntity) {
        if(action == CounterAdapter.Companion.Action.INCREMENT) {
            incrementCounter(counter)
        }
        else {
            decrementCounter(counter)
        }
    }

    fun observeCounters() = counters.counters

    fun getCounters() = counters.execute()

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

}
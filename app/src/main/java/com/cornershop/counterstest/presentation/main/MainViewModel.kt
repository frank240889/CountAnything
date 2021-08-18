package com.cornershop.counterstest.presentation.main

import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.domain.Counter
import com.cornershop.counterstest.usecase.Counters
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val counters: Counters
): ViewModel() {
    fun performAction(action: CounterAdapter.Companion.Action, counter: Counter) {

    }

}
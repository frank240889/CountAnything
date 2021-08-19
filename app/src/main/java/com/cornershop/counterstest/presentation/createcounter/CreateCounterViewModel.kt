package com.cornershop.counterstest.presentation.createcounter

import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.usecase.CreateCounter
import javax.inject.Inject

class CreateCounterViewModel @Inject constructor(
    private val createCounter: CreateCounter
) : ViewModel() {

    fun observeCounterCreation() = createCounter.response

    fun createCounter() = createCounter.execute()

    fun setTitle(title: String) {
        createCounter.counterTitle = title
    }
}
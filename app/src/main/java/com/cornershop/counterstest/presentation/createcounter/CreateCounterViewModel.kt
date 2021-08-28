package com.cornershop.counterstest.presentation.createcounter

import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.domain.local.usecase.CreateCounter
import javax.inject.Inject

/**
 * The view model for create counter screen, it only serves as bridge between the UI and UseCases.
 */
class CreateCounterViewModel @Inject constructor(
    private val createCounter: CreateCounter
) : ViewModel() {

    fun observeCounterCreation() = createCounter.response

    fun createCounter() = createCounter.execute()

    fun setTitle(title: String) {
        createCounter.counterTitle = title
    }
}
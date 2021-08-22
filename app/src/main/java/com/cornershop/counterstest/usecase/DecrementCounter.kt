package com.cornershop.counterstest.usecase

import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.data.CounterRepository
import com.cornershop.counterstest.domain.local.CounterEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class DecrementCounter @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: CounterRepository
): UseCase() {

    lateinit var counterEntity: CounterEntity


    private var job: Job? = null

    override fun execute() {
        job?.cancel()
        val coroutineScope = CoroutineScope(dispatcher)
        job = coroutineScope.launch {
            counterRepository.decrementCounterByOne(counterEntity)
        }

    }

    override fun release() {
        job?.cancel()
    }
}
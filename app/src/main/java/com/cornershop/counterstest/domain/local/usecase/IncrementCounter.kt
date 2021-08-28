package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.interfaces.Repository
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.interfaces.ExceptionHandler
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Represents the action from user to update a counter same as [DecrementCounter].
 */
class IncrementCounter @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: Repository,
    private val errorHandler: ExceptionHandler
): UseCase() {

    lateinit var counterEntity: CounterEntity

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is CancellationException) {
            _response.postValue(State.Error(errorHandler.handleError(throwable)))
        }
    }

    private val _response: MutableLiveData<State<String>> by lazy {
        MediatorLiveData()
    }

    val response: LiveData<State<String>> get() = _response

    private var job: Job? = null

    override fun execute() {
        job?.cancel()
        val coroutineScope = CoroutineScope(dispatcher + coroutineExceptionHandler)
        job = coroutineScope.launch {
            _response.postValue(State.Loading(true))
            counterRepository.incrementCounterByOne(counterEntity)
        }
    }

    override fun release() {
        job?.cancel()
    }
}
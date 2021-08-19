package com.cornershop.counterstest.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.ErrorHandler
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.CounterRepository
import com.cornershop.counterstest.domain.local.CounterEntity
import kotlinx.coroutines.*
import javax.inject.Inject

class Counters @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: CounterRepository,
    private val errorHandler: ErrorHandler
): UseCase() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is CancellationException) {
            _counters.postValue(State.Error(errorHandler.handleError(throwable)))
        }
    }

    private val _counters: MediatorLiveData<State<List<CounterEntity>>> by lazy {
        MediatorLiveData()
    }

    val counters: LiveData<State<List<CounterEntity>>> get() = _counters

    private val coroutineScope = CoroutineScope(coroutineExceptionHandler)

    private var job: Job? = null

    init {
        _counters.addSource(counterRepository.counters()) { counters ->
            _counters.value = State.Success(counters)
        }
    }

    override fun execute() {
        job?.cancel()
        job = coroutineScope.launch(dispatcher) {
            _counters.postValue(State.Loading(true))
            counterRepository.fetchCounters()
            _counters.postValue(State.Loading(false))
        }
    }

    override fun release() {
        job?.cancel()
        _counters.removeSource(counterRepository.counters())
    }
}
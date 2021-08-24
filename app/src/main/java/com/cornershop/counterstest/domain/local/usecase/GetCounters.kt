package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.ErrorHandler
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.CounterRepository
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import kotlinx.coroutines.*
import javax.inject.Inject

class GetCounters @Inject constructor(
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

    var forceRefresh = false

    private var job: Job? = null

    init {
        _counters.addSource(counterRepository.localCountersObservable()) { counters ->
            _counters.value = State.Success(counters)
        }
    }

    override fun execute() {
        job?.cancel()
        job = CoroutineScope(coroutineExceptionHandler).launch(dispatcher) {
            _counters.postValue(State.Loading(true))
            counterRepository.fetchCounters(forceRefresh)
        }
    }

    override fun release() {
        job?.cancel()
        _counters.removeSource(counterRepository.localCountersObservable())
    }
}
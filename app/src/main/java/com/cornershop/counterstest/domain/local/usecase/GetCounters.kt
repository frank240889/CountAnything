package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.interfaces.Repository
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.interfaces.ExceptionHandler
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Represents the action from user fetch counters.
 */
class GetCounters @Inject constructor (
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: Repository,
    private val errorHandler: ExceptionHandler
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

    // Flag to indicate a full refresh.
    var forceRefresh = false

    private var job: Job? = null

    init {
        // A mediator live data to observe response from local DB and wrap the data into a
        // State object.
        _counters.addSource(counterRepository.localCountersObservable()) { counters ->
            _counters.value = State.Success(counters)
        }
    }

    override fun execute() {
        job?.cancel()
        job = CoroutineScope(coroutineExceptionHandler).launch(dispatcher) {
            _counters.postValue(State.Loading(true))
            val dataSize = counterRepository.fetchCounters(forceRefresh)
            if (dataSize == 0) {
                _counters.postValue(State.Success(emptyList()))
            }
        }
    }

    override fun release() {
        job?.cancel()
        // is required to remove the datasource for this mediator live data.
        _counters.removeSource(counterRepository.localCountersObservable())
    }
}
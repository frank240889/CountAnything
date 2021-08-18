package com.cornershop.counterstest.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.ErrorHandler
import com.cornershop.counterstest.common.ResourceManager
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.local.CounterRepository
import com.cornershop.counterstest.domain.Counter
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

    private val _counters: MutableLiveData<State<List<Counter>>> by lazy {
        MutableLiveData()
    }

    val counters: LiveData<State<List<Counter>>> get() = _counters

    private val coroutineScope = CoroutineScope(coroutineExceptionHandler)

    private var job: Job? = null

    override fun execute() {
        job?.cancel()
        job = coroutineScope.launch(dispatcher) {
            _counters.postValue(State.Loading(true))
            counterRepository.counters().let { counters ->
                _counters.postValue(State.Success(counters))
            }
            _counters.postValue(State.Loading(false))
        }
    }

    override fun release() {
        job?.cancel()
    }
}
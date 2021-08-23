package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.ErrorHandler
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.CounterRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class SyncCounters @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: CounterRepository,
    private val errorHandler: ErrorHandler
): UseCase() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is CancellationException) {
            _response.postValue(State.Error(errorHandler.handleError(throwable)))
        }
    }

    private val _response: MutableLiveData<State<Unit>> by lazy {
        MediatorLiveData()
    }

    val response: LiveData<State<Unit>> get() = _response

    private var job: Job? = null

    override fun execute() {
        job?.cancel()
        val coroutineScope = CoroutineScope(coroutineExceptionHandler)
        job = coroutineScope.launch(dispatcher) {
            val needsSync = counterRepository.getCountersPendingToUpdate().isNotEmpty()
            if (counterRepository.localCount() > 0 && needsSync) {
                _response.postValue(State.Loading(true))
                if (counterRepository.syncLocalCounters()) {
                    _response.postValue(State.Success(Unit))
                }
                else {
                    _response.postValue(State.Loading(false))
                }
            }
        }

    }

    override fun release() {
        job?.cancel()
    }
}
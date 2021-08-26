package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CounterValidator
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.Repository
import com.cornershop.counterstest.domain.remote.CounterName
import com.cornershop.counterstest.exceptions.InvalidTitleCounterException
import com.cornershop.counterstest.interfaces.ExceptionHandler
import kotlinx.coroutines.*
import javax.inject.Inject

@CustomAnnotations.OpenForTesting
class CreateCounter @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: Repository,
    private val errorHandler: ExceptionHandler,
    private val counterValidator: CounterValidator
): UseCase() {

    var counterTitle: String? = null

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
        if (counterValidator.isTitleValid(counterTitle)) {
            job = coroutineScope.launch(dispatcher) {
                _response.postValue(State.Loading(true))
                counterRepository.createLocalCounter(CounterName(counterTitle!!))
                _response.postValue(State.Success(Unit))
            }
        }
        else {
            _response.value = State.Error(errorHandler.handleError(InvalidTitleCounterException()))
            _response.value = State.Loading(false)
        }

    }

    override fun release() {
        job?.cancel()
    }
}
package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CounterNameValidator
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.interfaces.Repository
import com.cornershop.counterstest.domain.remote.CounterName
import com.cornershop.counterstest.exceptions.InvalidTitleCounterException
import com.cornershop.counterstest.interfaces.ExceptionHandler
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Represents the action from user to create a new counter.
 */
@CustomAnnotations.OpenForTesting
class CreateCounter @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: Repository,
    private val errorHandler: ExceptionHandler,
    private val counterNameValidator: CounterNameValidator
): UseCase() {

    var counterTitle: String? = null

    // An error handler, returns a message in case a error is thrown, except for CancellationException
    // because we don't need to show to user that message every time the coroutine is canceled.
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is CancellationException) {
            _response.postValue(State.Error(errorHandler.handleError(throwable)))
        }
    }

    // Exposes the response to this use case.
    private val _response: MutableLiveData<State<Unit>> by lazy {
        MediatorLiveData()
    }

    val response: LiveData<State<Unit>> get() = _response

    // The reference to job created by coroutine.
    private var job: Job? = null

    override fun execute() {
        // Cancel previous task if exist.
        job?.cancel()
        val coroutineScope = CoroutineScope(coroutineExceptionHandler)
        if (counterNameValidator.isTitleValid(counterTitle)) {
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
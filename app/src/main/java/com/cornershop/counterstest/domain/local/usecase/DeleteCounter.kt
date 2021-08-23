package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.ErrorHandler
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.CounterRepository
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import kotlinx.coroutines.*
import javax.inject.Inject

class DeleteCounter @Inject constructor(
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

    private val countersToDelete: MutableList<CounterEntity> by lazy {
        ArrayList()
    }

    val response: LiveData<State<Unit>> get() = _response

    private var job: Job? = null

    override fun execute() {
        job?.cancel()
        val coroutineScope = CoroutineScope(coroutineExceptionHandler)
        job = coroutineScope.launch(dispatcher) {
            _response.postValue(State.Loading(true))
            countersToDelete.forEach { counterEntity ->
                counterRepository.deleteCounter(counterEntity)
            }
            _response.postValue(State.Success(Unit))
        }

    }

    override fun release() {
        job?.cancel()
    }

    fun addToDelete(counter: CounterEntity) {
        if (countersToDelete.find { it.id == counter.id } == null) {
            countersToDelete.add(counter)
        }
        else {
            countersToDelete.remove(counter)
        }

    }

    fun clearDeletionList() = countersToDelete.clear()

    fun getCountersName() = countersToDelete.joinToString {
        it.title
    }
}
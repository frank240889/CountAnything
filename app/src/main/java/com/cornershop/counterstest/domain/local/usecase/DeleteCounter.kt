package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.Repository
import com.cornershop.counterstest.data.local.cache.Cache
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.interfaces.ExceptionHandler
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class DeleteCounter @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: Repository,
    private val errorHandler: ExceptionHandler,
    private val cache: Cache<CounterEntity, MutableList<CounterEntity>>
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

    private val queue: Deque<String> by lazy {
        ArrayDeque()
    }

    override fun execute() {
        job?.cancel()
        val coroutineScope = CoroutineScope(coroutineExceptionHandler)
        job = coroutineScope.launch(dispatcher) {
            _response.postValue(State.Loading(true))
            cache.data().forEach { counterEntity ->
                counterRepository.deleteCounter(counterEntity)
                queue.add(counterEntity.id)
            }
            removeInCacheData()
            _response.postValue(State.Success(Unit))
        }
    }

    private fun removeInCacheData() {
        queue.forEach { id ->
            cache
                .data()
                .find { counterEntity ->
                    counterEntity.id == id
                }?.let { entityFound ->
                    cache.remove(entityFound)
                    queue.pop()
                }
        }
    }

    override fun release() {
        job?.cancel()
    }

    fun getCountersName() = cache.data().joinToString { it.title }
}
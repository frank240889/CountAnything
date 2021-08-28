package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.interfaces.Repository
import com.cornershop.counterstest.interfaces.Cache
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.interfaces.ExceptionHandler
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

/**
 * Represents the action from user to delete one or multiple counters. First are deleted from remote source
 * and added to a queue to later delete from local DB.
 */
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

    /**
     * A queue to mark the counters deleted from remote and then delete cache.
     */
    private val queue: Deque<String> by lazy {
        ArrayDeque()
    }

    override fun execute() {
        job?.cancel()
        val coroutineScope = CoroutineScope(coroutineExceptionHandler)
        job = coroutineScope.launch(dispatcher) {
            _response.postValue(State.Loading(true))
            // Delete all counters stored in cache.
            cache.data().forEach { counterEntity ->
                counterRepository.deleteCounter(counterEntity)
                // The add to queue to remove from cache
                queue.add(counterEntity.id)
            }
            removeInCacheData()
            _response.postValue(State.Success(Unit))
        }
    }

    /**
     * Remove counters in cache.
     */
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

    /**
     * Returns a string of all counters in cache.
     */
    fun getCountersName() = cache.data().joinToString { it.title }
}
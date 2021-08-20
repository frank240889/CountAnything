package com.cornershop.counterstest.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.CounterValidator
import com.cornershop.counterstest.common.CustomAnnotations
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.CounterRepository
import com.cornershop.counterstest.domain.local.CounterEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchCounter @Inject constructor(
    @CustomAnnotations.IODispatcher private val dispatcher: CoroutineDispatcher,
    private val counterRepository: CounterRepository,
    private val validator: CounterValidator
): UseCase() {

    private val _searchResults: MutableLiveData<State<List<CounterEntity>>> by lazy {
        MediatorLiveData()
    }

    var title: String? = null

    val searchResults: LiveData<State<List<CounterEntity>>> get() = _searchResults

    private val coroutineScope = CoroutineScope(dispatcher)

    private var job: Job? = null

    override fun execute() {
        job?.cancel()
        if (validator.isTitleValid(title)) {
            job = coroutineScope.launch {
                _searchResults.postValue(State.Loading(true))
                counterRepository.search(title!!).let { results ->
                    _searchResults.postValue(State.Success(results))
                }
                _searchResults.postValue(State.Loading(false))
            }
        }
    }

    override fun release() {
        job?.cancel()
    }
}
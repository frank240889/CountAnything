package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.cornershop.counterstest.data.Repository
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import javax.inject.Inject

class SearchCounter @Inject constructor(
    private val counterRepository: Repository
): UseCase() {

    var title: String? = null

    private val _resultSearch: MediatorLiveData<List<CounterEntity>> by lazy {
        MediatorLiveData()
    }

    val resultSearch: LiveData<List<CounterEntity>> get() = _resultSearch

    private val observer: Observer<List<CounterEntity>> by lazy {
        Observer { data ->
            _resultSearch.value = searchIn(title, data)
            _resultSearch.removeSource(counterRepository.localCountersObservable())
        }
    }

    override fun execute() {
        if (title.isNullOrEmpty()) {
            _resultSearch.value = ArrayList()
        }
        else {
            _resultSearch.addSource(counterRepository.localCountersObservable(), observer)
        }
    }

    private fun searchIn(title: String?, data: List<CounterEntity>) = title
        ?.run {
            data.filter {
                it.title.contains(title)
            }
        } ?: ArrayList()
}
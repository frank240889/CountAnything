package com.cornershop.counterstest.presentation.searchcounter

import com.cornershop.counterstest.data.local.cache.Cache
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.local.usecase.*
import com.cornershop.counterstest.presentation.main.MainViewModel
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchCounter: SearchCounter,
    getCounters: GetCounters,
    incrementCounter: IncrementCounter,
    decrementCounter: DecrementCounter,
    deleteCounter: DeleteCounter,
    shareCounter: ShareCounter,
    cache: Cache<CounterEntity, List<CounterEntity>>
): MainViewModel(
    getCounters,
    incrementCounter,
    decrementCounter,
    deleteCounter,
    shareCounter,
    cache
) {

    fun observeSearchResults() = searchCounter.resultSearch

    fun search(title: String) {
        searchCounter.title = title
        searchCounter.execute()
    }
}
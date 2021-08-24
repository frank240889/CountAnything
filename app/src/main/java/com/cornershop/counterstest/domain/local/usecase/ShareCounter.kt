package com.cornershop.counterstest.domain.local.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.data.local.cache.Cache
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import javax.inject.Inject

class ShareCounter @Inject constructor(
    private val cache: Cache<CounterEntity, List<CounterEntity>>
): UseCase() {

    private val _response: MutableLiveData<State<String>> by lazy {
        MediatorLiveData()
    }

    val response: LiveData<State<String>> get() = _response

    override fun execute() {
        _response.value = State.Success(createShareableStringFromData())
    }

    private fun createShareableStringFromData() = cache.data().joinToString(
        separator = "\n",
        transform = { counterEntity ->
            counterEntity.count.toString().plus(" x ").plus(counterEntity.title)
        }
    )
}
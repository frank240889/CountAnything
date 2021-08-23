package com.cornershop.counterstest.presentation.examplescountername

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.ResourceManager
import java.util.*
import javax.inject.Inject

class ExamplesViewModel @Inject constructor(
    private val resourceManager: ResourceManager
) : ViewModel() {
    private val _topic1: MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData()
    }
    val topic1: LiveData<MutableList<String>> get() = _topic1

    private val _topic2: MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData()
    }
    val topic2: LiveData<MutableList<String>> get() = _topic2

    private val _topic3: MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData()
    }
    val topic3: LiveData<MutableList<String>> get() = _topic3

    init {
        _topic1.value = createData(getHeader(R.string.topic_1), getArray(R.array.drinks_array))
        _topic2.value = createData(getHeader(R.string.topic_2), getArray(R.array.food_array))
        _topic3.value = createData(getHeader(R.string.topic_3), getArray(R.array.misc_array))
    }


    private fun getHeader(id: Int) = resourceManager.getString(id)

    private fun getArray(id: Int) = resourceManager.getStringArray(id)

    private fun createData(header: String, data: Array<String>) =
        LinkedList<String>().apply {
            add(0, header)
            addAll(data)
        }
}
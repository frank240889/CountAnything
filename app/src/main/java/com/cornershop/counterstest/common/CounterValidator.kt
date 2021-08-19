package com.cornershop.counterstest.common

import javax.inject.Inject

class CounterValidator @Inject constructor() {

    fun isTitleValid(value: String?) = !value.isNullOrBlank() && value.isNotEmpty()
}
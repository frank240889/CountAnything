package com.cornershop.counterstest.common

import javax.inject.Inject

@CustomAnnotations.OpenForTesting
class CounterValidator @Inject constructor() {

    fun isTitleValid(value: String?) = !value.isNullOrBlank() && value.isNotEmpty()
}
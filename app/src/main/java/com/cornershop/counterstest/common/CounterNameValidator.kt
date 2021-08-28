package com.cornershop.counterstest.common

import javax.inject.Inject

/**
 * A helper class to validate the name of counter.
 */
@CustomAnnotations.OpenForTesting
class CounterNameValidator @Inject constructor() {

    fun isTitleValid(value: String?) = !value.isNullOrBlank() && value.isNotEmpty()
}
package com.cornershop.counterstest.common

sealed class State<out T: Any> {
    data class Success<out T: Any>(val value: T): State<T>()
    data class Error(val message: String): State<Nothing>()
    data class Loading(val loading: Boolean): State<Nothing>()
}

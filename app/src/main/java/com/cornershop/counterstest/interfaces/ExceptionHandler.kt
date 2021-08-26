package com.cornershop.counterstest.interfaces

interface ExceptionHandler {
    fun handleError(t: Throwable): String
}
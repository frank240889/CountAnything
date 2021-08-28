package com.cornershop.counterstest.interfaces

/**
 * Component to validate the throwable type and return a message.
 */
interface ExceptionHandler {
    fun handleError(t: Throwable): String
}
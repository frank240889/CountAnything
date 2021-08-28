package com.cornershop.counterstest.domain.local.usecase

/**
 * A generic class to represent user actions.
 */
abstract class UseCase {
    /**
     * Performs the action.
     */
    abstract fun execute()

    /**
     * Clear resources used in use case.
     */
    open fun release(){}
}
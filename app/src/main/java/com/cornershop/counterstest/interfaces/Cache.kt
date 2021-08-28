package com.cornershop.counterstest.interfaces

/**
 * A interface to provide a simple way to store arbitrary data.
 */
interface Cache<I, D> {
    fun add(item: I): Boolean
    fun remove(item: I): Boolean
    fun contains(item: I): Boolean
    fun clear()

    /**
     * Returns all data.
     */
    fun data(): D
}
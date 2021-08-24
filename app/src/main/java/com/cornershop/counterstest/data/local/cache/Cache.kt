package com.cornershop.counterstest.data.local.cache

interface Cache<I, D> {
    fun add(item: I): Boolean
    fun remove(item: I): Boolean
    fun contains(item: I): Boolean
    fun clear()
    fun data(): D
}
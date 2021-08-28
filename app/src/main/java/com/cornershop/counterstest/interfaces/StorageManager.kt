package com.cornershop.counterstest.interfaces

/**
 * Provides a simple way to store booleans.
 */
interface StorageManager {
    fun saveBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
}
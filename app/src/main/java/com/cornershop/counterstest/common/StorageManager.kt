package com.cornershop.counterstest.common

interface StorageManager {
    fun saveBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
}
package com.cornershop.counterstest.interfaces

import androidx.annotation.StringRes

/**
 * Component to provide access to string resources without interact directly with context.
 */
interface ResourceManager {

    /**
     * Returns the string for [id]
     */
    fun getString(@StringRes id: Int): String

    /**
     * Returns the array string for [id]
     */
    fun getStringArray(@StringRes id: Int): Array<String>
}
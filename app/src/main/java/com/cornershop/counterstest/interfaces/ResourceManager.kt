package com.cornershop.counterstest.interfaces

import androidx.annotation.StringRes

interface ResourceManager {

    fun getString(@StringRes id: Int): String

    fun getStringArray(@StringRes id: Int): Array<String>
}
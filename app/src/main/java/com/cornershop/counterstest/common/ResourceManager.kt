package com.cornershop.counterstest.common

import androidx.annotation.StringRes

interface ResourceManager {

    fun getString(@StringRes id: Int): String
}
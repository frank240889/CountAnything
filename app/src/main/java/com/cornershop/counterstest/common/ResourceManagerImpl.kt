package com.cornershop.counterstest.common

import android.content.Context
import javax.inject.Inject

class ResourceManagerImpl @Inject constructor(
    private val context: Context
): ResourceManager {
    override fun getString(id: Int) = context.getString(id)
}
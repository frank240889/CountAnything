package com.cornershop.counterstest.common

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.cornershop.counterstest.interfaces.ResourceManager
import javax.inject.Inject

/**
 * Implementation of [ResourceManager]
 */
class ResourceManagerImpl @Inject constructor(
    private val context: Context
): ResourceManager {
    override fun getString(@StringRes id: Int) = context.getString(id)

    override fun getStringArray(@ArrayRes id: Int): Array<String> = context.resources.getStringArray(id)
}
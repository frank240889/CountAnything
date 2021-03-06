package com.cornershop.counterstest.common

import android.content.Context
import android.content.SharedPreferences
import com.cornershop.counterstest.BuildConfig
import com.cornershop.counterstest.interfaces.StorageManager
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val context: Context
): StorageManager {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value ).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean) = sharedPreferences.getBoolean(key, defaultValue)
}
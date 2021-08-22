package com.cornershop.counterstest.presentation.welcome

import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.common.StorageManager
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(
    private val storeManager: StorageManager
): ViewModel() {
    companion object {
        const val SHOW_WELCOME_SCREEN = "show_welcome_screen"
    }

    fun skipWelcomeScreen() = storeManager.getBoolean(SHOW_WELCOME_SCREEN)
    fun setSkipWelcomeScreen() = storeManager.saveBoolean(SHOW_WELCOME_SCREEN, true)
}
package com.cornershop.counterstest.common

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cornershop.counterstest.interfaces.ResourceManager
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorHandlerTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var resourceManager: ResourceManager
    private lateinit var errorHandler: ErrorHandler

    @Before
    fun setup() {
        resourceManager = ResourceManagerImpl(context)
        errorHandler = ErrorHandler(resourceManager)
    }

    @Test
    fun verify_message_is_not_null_when_unknown_exception_evaluated() {
        assertNotNull(errorHandler.handleError(RuntimeException()))
    }
}
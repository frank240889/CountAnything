package com.cornershop.counterstest.common

import com.cornershop.counterstest.R
import com.cornershop.counterstest.exceptions.InvalidTitleCounterException
import com.cornershop.counterstest.exceptions.NotItemsSelectedForDeletion
import com.cornershop.counterstest.interfaces.ExceptionHandler
import com.cornershop.counterstest.interfaces.ResourceManager
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import javax.inject.Inject

/**
 * Implementation of [ExceptionHandler]
 */
@CustomAnnotations.OpenForTesting
class ErrorHandler @Inject constructor(
    private val resourceManager: ResourceManager
): ExceptionHandler {
    override fun handleError(t: Throwable) = when (t) {
        is HttpException,
        is ConnectException,
        is SocketException,
        is InterruptedIOException -> resourceManager.getString(R.string.connection_error_description)
        is InvalidTitleCounterException -> resourceManager.getString(R.string.invalid_title_counter)
        is NotItemsSelectedForDeletion -> resourceManager.getString(R.string.no_items_to_delete)
        else -> resourceManager.getString(R.string.generic_error_description)
    }

}
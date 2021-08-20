package com.cornershop.counterstest.common

import com.cornershop.counterstest.R
import com.cornershop.counterstest.exceptions.InvalidTitleCounterException
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class ErrorHandler @Inject constructor(
    private val resourceManager: ResourceManager
) {
    fun handleError(t: Throwable) = when (t) {
        is HttpException -> resourceManager.getString(R.string.connection_error_description)
        is ConnectException -> resourceManager.getString(R.string.connection_error_description)
        is InvalidTitleCounterException -> resourceManager.getString(R.string.invalid_title_counter)
        else -> resourceManager.getString(R.string.generic_error_description)
    }

}
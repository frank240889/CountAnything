package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.common.ErrorHandler
import com.cornershop.counterstest.interfaces.ExceptionHandler
import dagger.Binds
import dagger.Module

@Module
abstract class ErrorHandlerModule {
    @Binds
    abstract fun provideErrorHandler(errorHandler: ErrorHandler): ExceptionHandler
}
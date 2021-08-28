package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.common.CustomAnnotations
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * This module provides the dispatchers for coroutines. As per Android docs, is good
 * practice to inject the dispatcher to use instead of hardcoding
 */
@Module
class CoroutineDispatcherModule {
    @CustomAnnotations.IODispatcher
    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @CustomAnnotations.MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
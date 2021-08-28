package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.common.ResourceManagerImpl
import com.cornershop.counterstest.interfaces.ResourceManager
import dagger.Binds
import dagger.Module

/**
 * This module provides the resource manager.
 */
@Module
abstract class ResourceManagerModule {

    @Binds
    abstract fun provideResourceManager(resourceManagerImpl: ResourceManagerImpl): ResourceManager
}
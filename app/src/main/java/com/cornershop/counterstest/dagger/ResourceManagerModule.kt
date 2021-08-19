package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.common.ResourceManager
import com.cornershop.counterstest.common.ResourceManagerImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ResourceManagerModule {

    @Binds
    abstract fun provideResourceManager(resourceManagerImpl: ResourceManagerImpl): ResourceManager
}
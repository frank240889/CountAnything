package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.common.PreferenceManager
import com.cornershop.counterstest.common.StorageManager
import dagger.Binds
import dagger.Module

@Module
abstract class StoreManagerModule {

    @Binds
    abstract fun provideStoreManager(preferenceManager: PreferenceManager): StorageManager
}
package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.common.PreferenceManager
import com.cornershop.counterstest.common.StorageManager
import com.cornershop.counterstest.data.CounterRepository
import com.cornershop.counterstest.data.Repository
import com.cornershop.counterstest.data.local.cache.Cache
import com.cornershop.counterstest.data.local.cache.InMemoryCache
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import dagger.Binds
import dagger.Module

@Module
abstract class StoreManagerModule {

    @Binds
    abstract fun provideStoreManager(preferenceManager: PreferenceManager): StorageManager

    @Binds
    abstract fun provideCache(cache: InMemoryCache): Cache<CounterEntity, List<CounterEntity>>

    @Binds
    abstract fun provideRepository(counterRepository: CounterRepository):Repository
}
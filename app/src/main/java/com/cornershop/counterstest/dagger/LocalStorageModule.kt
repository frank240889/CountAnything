package com.cornershop.counterstest.dagger

import android.app.Application
import com.cornershop.counterstest.data.local.database.CountersDatabase
import dagger.Module
import dagger.Provides

/**
 * This module provides the storage related dependencies.
 */
@Module
class LocalStorageModule {

    @Provides
    fun providesCounterDatabase(application: Application) = CountersDatabase.getInstance(application)

    @Provides
    fun providesCounterDao(countersDatabase: CountersDatabase) = countersDatabase.countersDao()
}
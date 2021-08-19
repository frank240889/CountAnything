package com.cornershop.counterstest.dagger

import android.app.Application
import com.cornershop.counterstest.data.local.CountersDatabase
import dagger.Module
import dagger.Provides

@Module
class LocalStorageModule {

    @Provides
    fun providesCounterDatabase(application: Application) = CountersDatabase.getInstance(application)
}
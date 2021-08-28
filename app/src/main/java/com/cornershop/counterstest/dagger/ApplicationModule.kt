package com.cornershop.counterstest.dagger

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * The app module provides the application context.
 */
@Module
class ApplicationModule {

    @Provides
    fun providesApplicationContext(application: Application): Context = application
}
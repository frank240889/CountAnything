package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.presentation.MainActivity
import com.cornershop.counterstest.presentation.welcome.WelcomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * The module to inject dependencies into activities. This module will tell dagger to generate an
 * injector for those activities.
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeWelcomeActivity(): WelcomeActivity
}
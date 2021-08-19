package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.presentation.main.MainActivity
import com.cornershop.counterstest.presentation.welcome.WelcomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeWelcomeActivity(): WelcomeActivity
}
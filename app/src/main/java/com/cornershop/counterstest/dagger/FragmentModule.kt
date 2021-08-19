package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.presentation.createcounter.CreateCounterFragment
import com.cornershop.counterstest.presentation.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateCounterFragment(): CreateCounterFragment
}
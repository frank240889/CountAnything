package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.presentation.main.MainActivity
import com.cornershop.counterstest.presentation.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment
}
package com.cornershop.counterstest.dagger

import com.cornershop.counterstest.presentation.createcounter.CreateCounterFragment
import com.cornershop.counterstest.presentation.examplescountername.ExamplesFragment
import com.cornershop.counterstest.presentation.main.MainFragment
import com.cornershop.counterstest.presentation.searchcounter.SearchResultsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateCounterFragment(): CreateCounterFragment

    @ContributesAndroidInjector
    abstract fun contributeExamplesFragment(): ExamplesFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchResultsFragment(): SearchResultsFragment
}
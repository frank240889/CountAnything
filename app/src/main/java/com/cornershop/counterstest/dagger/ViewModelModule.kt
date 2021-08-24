package com.cornershop.counterstest.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cornershop.counterstest.common.ViewModelFactory
import com.cornershop.counterstest.presentation.createcounter.CreateCounterViewModel
import com.cornershop.counterstest.presentation.examplescountername.ExamplesViewModel
import com.cornershop.counterstest.presentation.main.MainViewModel
import com.cornershop.counterstest.presentation.searchcounter.SearchViewModel
import com.cornershop.counterstest.presentation.welcome.WelcomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    abstract fun provideWelcomeViewModel(welcomeViewModel: WelcomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateCounterViewModel::class)
    abstract fun provideCreateCounterViewModel(createCounterViewModel: CreateCounterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExamplesViewModel::class)
    abstract fun provideExamplesViewModel(examplesViewModel: ExamplesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun provideSearchViewModel(searchViewModel: SearchViewModel): ViewModel
}
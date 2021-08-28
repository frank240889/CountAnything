package com.cornershop.counterstest.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.common.ViewModelFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Class to encapsulate the common functionality for derived classes.
 */
abstract class BaseViewModelFragment<VM: ViewModel> : Fragment(), HasAndroidInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Any>

    abstract val viewModel: VM

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any> = injector

    open fun showLoading(loading: Boolean) {}

    abstract fun provideViewModel(): VM
}
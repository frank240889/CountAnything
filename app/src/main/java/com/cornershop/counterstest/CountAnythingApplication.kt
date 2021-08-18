package com.cornershop.counterstest

import android.app.Application
import com.cornershop.counterstest.dagger.DaggerApplicationComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class CountAnythingApplication: Application(), HasAndroidInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Any>
    override fun onCreate() {
        super.onCreate()

        DaggerApplicationComponent
            .builder()
            .application(this)
            .build()?.inject(this)
    }

    override fun androidInjector() = injector
}
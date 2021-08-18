package com.cornershop.counterstest.dagger

import android.app.Application
import com.cornershop.counterstest.CountAnythingApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(
    modules =[
        AndroidInjectionModule::class
    ]
)
interface ApplicationComponent: AndroidInjector<CountAnythingApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent?
    }

    override fun inject(instance: CountAnythingApplication)
}
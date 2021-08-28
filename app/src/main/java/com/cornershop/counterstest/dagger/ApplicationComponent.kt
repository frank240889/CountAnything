package com.cornershop.counterstest.dagger

import android.app.Application
import com.cornershop.counterstest.CountAnythingApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * The component to create the dependencies graph using the next modules.
 */
@Component(
    modules =[
        AndroidInjectionModule::class,
        ApplicationModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ApiModule::class,
        CoroutineDispatcherModule::class,
        ViewModelModule::class,
        ResourceManagerModule::class,
        StoreManagerModule::class,
        LocalStorageModule::class,
        ErrorHandlerModule::class
    ]
)
@Singleton
interface ApplicationComponent: AndroidInjector<CountAnythingApplication> {

    @Component.Builder
    interface Builder {

        /**
         *
         */
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent?
    }

    override fun inject(instance: CountAnythingApplication)
}
package com.cornershop.counterstest.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


object Utils {
    const val TEXT_PLAIN = "text/plain"
    const val DEFAULT_ANIMATION_TIME = 250L
    const val OPAQUE = 1F
    const val TRANSPARENT = 0F
    const val COUNT = "count"

    /**
     * Method to add a observer to livedata and return its value in a sync way.
     */
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    /**
     * Helper method to return a string from a [CounterEntity] list
     */
    fun List<CounterEntity>.joinToStringWithDefault(): String {
        return this.joinToString(
            separator = "\n",
            transform = { counterEntity ->
                counterEntity.count.toString().plus(" x ").plus(counterEntity.title)
            }
        )
    }
}
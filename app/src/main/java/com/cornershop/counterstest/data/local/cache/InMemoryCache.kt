package com.cornershop.counterstest.data.local.cache

import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.interfaces.Cache
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A cache to store temporary data while app is alive.
 */
@Singleton
class InMemoryCache @Inject constructor(): Cache<CounterEntity, MutableList<CounterEntity>> {
    val data: MutableList<CounterEntity> by lazy {
        ArrayList()
    }
    override fun add(item: CounterEntity) = data.add(item)

    override fun remove(item: CounterEntity): Boolean {
        var removed = false
        data.apply {
            removed = this.remove(find { it.id == item.id })
        }
        return removed
    }

    override fun contains(item: CounterEntity) = data.find { it.id == item.id } != null

    override fun clear() = data.clear()

    override fun data() = data
}
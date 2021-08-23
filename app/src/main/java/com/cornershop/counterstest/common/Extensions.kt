package com.cornershop.counterstest.common

import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.remote.Counter

fun Counter.toCounterEntity(): CounterEntity = CounterEntity(this.id, this.title, this.count)
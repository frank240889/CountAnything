package com.cornershop.counterstest

import org.mockito.Mockito
inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
package com.cornershop.counterstest

import com.cornershop.counterstest.common.CounterValidator
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CounterValidatorTest {

    @Mock
    private lateinit var counterValidator: CounterValidator

    @Test
    fun `return true when validator success`() {
        MockitoAnnotations.openMocks(this)
        val name = "Test name"
        `when`(counterValidator.isTitleValid(name)).thenReturn(true)
    }

    @Test
    fun `return false when validator fails`() {
        MockitoAnnotations.openMocks(this)
        val name = ""
        `when`(counterValidator.isTitleValid(name)).thenReturn(false)
    }

}
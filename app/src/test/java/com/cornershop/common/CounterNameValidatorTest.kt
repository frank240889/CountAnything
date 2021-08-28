package com.cornershop.common

import com.cornershop.counterstest.common.CounterNameValidator
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CounterNameValidatorTest {

    @Mock
    private lateinit var counterNameValidator: CounterNameValidator

    @Test
    fun `return true when validator success`() {
        MockitoAnnotations.openMocks(this)
        val name = "Test name"
        `when`(counterNameValidator.isTitleValid(name)).thenReturn(true)
    }

    @Test
    fun `return false when validator fails`() {
        MockitoAnnotations.openMocks(this)
        val name = ""
        `when`(counterNameValidator.isTitleValid(name)).thenReturn(false)
    }

}
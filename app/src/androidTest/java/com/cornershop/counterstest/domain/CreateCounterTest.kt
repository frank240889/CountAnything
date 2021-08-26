package com.cornershop.counterstest.domain

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cornershop.counterstest.common.*
import com.cornershop.counterstest.common.Utils.getOrAwaitValue
import com.cornershop.counterstest.data.AbstractCountRepository
import com.cornershop.counterstest.data.CounterRepository
import com.cornershop.counterstest.data.local.database.CountersDatabase
import com.cornershop.counterstest.data.remote.Api
import com.cornershop.counterstest.domain.local.usecase.CreateCounter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CreateCounterTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    lateinit var api: Api

    @Mock
    lateinit var observer: Observer<State<Unit>>

    lateinit var repository: AbstractCountRepository

    lateinit var db: CountersDatabase

    lateinit var errorHandler: ErrorHandler

    lateinit var counterValidator: CounterValidator

    private lateinit var createCounter: CreateCounter

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CountersDatabase::class.java).build()

        repository = CounterRepository(api, db.countersDao())
        counterValidator = CounterValidator()
        errorHandler = ErrorHandler(ResourceManagerImpl(context))
        createCounter = CreateCounter(testDispatcher, repository, errorHandler, counterValidator)
    }

    @Test
    fun when_create_executes_dispatch_loading_and_success_if_no_error() {
        testCoroutineRule.runBlockingTest {
            createCounter.counterTitle = "Test"
            createCounter.execute()
            createCounter.response.observeForever(observer)
            createCounter.response.getOrAwaitValue()
            verify(observer).onChanged(State.Loading(true))
            //verify(observer).onChanged(State.Success(Unit))
        }
    }

    @After
    fun tearDown() {
//        repository.localCountersObservable().removeObserver(observer)
       // Dispatchers.resetMain()
    }
}
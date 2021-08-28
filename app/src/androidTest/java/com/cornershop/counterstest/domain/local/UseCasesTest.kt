package com.cornershop.counterstest.domain.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.cornershop.counterstest.common.CounterNameValidator
import com.cornershop.counterstest.common.ErrorHandler
import com.cornershop.counterstest.common.ResourceManagerImpl
import com.cornershop.counterstest.common.TestCoroutineRule
import com.cornershop.counterstest.common.Utils.getOrAwaitValue
import com.cornershop.counterstest.data.FakeRepository
import com.cornershop.counterstest.data.local.cache.InMemoryCache
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.domain.local.usecase.CreateCounter
import com.cornershop.counterstest.domain.local.usecase.DecrementCounter
import com.cornershop.counterstest.domain.local.usecase.DeleteCounter
import com.cornershop.counterstest.domain.local.usecase.IncrementCounter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UseCasesTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var repository: FakeRepository

    private lateinit var errorHandler: ErrorHandler

    private lateinit var counterNameValidator: CounterNameValidator

    private lateinit var cache: InMemoryCache

    private lateinit var createCounter: CreateCounter
    private lateinit var deleteCounter: DeleteCounter
    private lateinit var incrementCounter: IncrementCounter
    private lateinit var decrementCounter: DecrementCounter

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Dispatchers.setMain(testDispatcher)
        cache = InMemoryCache()
        repository = FakeRepository()
        counterNameValidator = CounterNameValidator()
        errorHandler = ErrorHandler(ResourceManagerImpl(context))
        createCounter = CreateCounter(testDispatcher, repository, errorHandler, counterNameValidator)
        deleteCounter = DeleteCounter(testDispatcher, repository, errorHandler, cache)
        incrementCounter = IncrementCounter(testDispatcher, repository, errorHandler)
        decrementCounter = DecrementCounter(testDispatcher, repository, errorHandler)

    }

    @Test
    fun when_create_counter_verify_response_is_a_no_empty_list() {
        testCoroutineRule.runBlockingTest {
            createCounter.counterTitle = "Test"
            createCounter.execute()
            createCounter.execute()
            createCounter.execute()
            assert(repository.localCountersObservable().getOrAwaitValue().isNotEmpty())
        }
    }

    @Test
    fun when_delete_counter_verify_list_decrements_by_one() {
        testCoroutineRule.runBlockingTest {
            createCounter.counterTitle = "Test"
            createCounter.execute()
            createCounter.execute()
            val oldSize = repository.localCountersObservable().getOrAwaitValue().size
            cache.add(CounterEntity("1","Test", 0))
            deleteCounter.execute()
            val newSize = repository.localCountersObservable().getOrAwaitValue().size
            assert(newSize < oldSize)
        }
    }

    @Test
    fun when_increment_counter_verify_list_has_same_size() {
        testCoroutineRule.runBlockingTest {
            val dataSize = repository.localCountersObservable().getOrAwaitValue().size
            incrementCounter.execute()
            assert(repository.localCountersObservable().getOrAwaitValue().size == dataSize)
        }
    }

    @Test
    fun when_decrement_counter_verify_list_has_same_size() {
        testCoroutineRule.runBlockingTest {
            val dataSize = repository.localCountersObservable().getOrAwaitValue().size
            decrementCounter.execute()
            assert(repository.localCountersObservable().getOrAwaitValue().size == dataSize)
        }
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}
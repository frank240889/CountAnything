package com.cornershop.counterstest.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cornershop.counterstest.common.Utils.getOrAwaitValue
import com.cornershop.counterstest.data.local.database.CountersDao
import com.cornershop.counterstest.data.local.database.CountersDatabase
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class WriteReadCounterEntityTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var dao: CountersDao
    private lateinit var db: CountersDatabase

    @Mock
    lateinit var observer: Observer<List<CounterEntity>>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CountersDatabase::class.java).build()
        dao = db.countersDao()
    }

    @Test
    fun write_counter_and_read_from_db() {
        val data = listOf(CounterEntity("1234", "Example", 0))
        dao.insert(data)
        dao.counters().observeForever(observer)
        assert(dao.counters().getOrAwaitValue().size == 1)
    }

    @After
    fun release() {
        db.close()
    }
}
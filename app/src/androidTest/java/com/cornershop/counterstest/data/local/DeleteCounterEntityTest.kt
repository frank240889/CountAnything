package com.cornershop.counterstest.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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

@RunWith(AndroidJUnit4::class)
class DeleteCounterEntityTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var dao: CountersDao
    private lateinit var db: CountersDatabase

    private val data: MutableList<CounterEntity> = mutableListOf(
        CounterEntity("1234", "Example1", 0),
        CounterEntity("4321", "Example2", 0),
        CounterEntity("1324", "Example3", 0),
        CounterEntity("1342", "Example4", 0),
        CounterEntity("1423", "Example5", 0),
        CounterEntity("1432", "Example6", 0)
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CountersDatabase::class.java).build()
        dao = db.countersDao()
        dao.insert(data)
    }

    @Test
    fun verify_one_element_deletion_is_correct() {
        val valueToDelete = CounterEntity("1432", "Example6", 6)
        dao.delete(valueToDelete)
        val counterDeleted = dao.counters().getOrAwaitValue().find { it.id == valueToDelete.id }
        assert(counterDeleted == null)
    }

    @Test
    fun verify_clear_db_is_correct() {
        dao.clear()
        assert(dao.counters().getOrAwaitValue().isEmpty())
    }

    @After
    fun release() {
        db.close()
    }
}
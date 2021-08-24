package com.cornershop.counterstest.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cornershop.counterstest.domain.local.entities.CounterEntity

@Dao
abstract class CountersDao {

    @Query("SELECT * FROM counter")
    abstract fun counters(): LiveData<List<CounterEntity>>

    @Query("SELECT COUNT(*) FROM counter")
    abstract suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(counterEntity: List<CounterEntity>)

    @Update
    abstract fun update(counterEntity: CounterEntity): Int

    @Delete
    abstract fun delete(counterEntity: CounterEntity)
}
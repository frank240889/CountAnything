package com.cornershop.counterstest.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cornershop.counterstest.domain.local.CounterEntity

@Dao
abstract class CountersDao {

    @Query("SELECT * FROM counter")
    abstract fun getCounters(): LiveData<List<CounterEntity>>

    @Query("SELECT COUNT(*) FROM counter")
    abstract suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCounters(counterEntity: List<CounterEntity>)

    @Update
    abstract fun updateCounter(counterEntity: CounterEntity)

    @Delete
    abstract fun delete(counterEntity: CounterEntity)

    @Query("SELECT * FROM counter WHERE counter.title like :query")
    abstract suspend fun search(query: String): List<CounterEntity>

}
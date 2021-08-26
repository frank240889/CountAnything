package com.cornershop.counterstest.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cornershop.counterstest.domain.local.entities.CounterEntity

@Dao
interface CountersDao {

    @Query("SELECT * FROM counter")
    fun counters(): LiveData<List<CounterEntity>>

    @Query("SELECT COUNT(*) FROM counter")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(counterEntity: List<CounterEntity>)

    @Update
    fun update(counterEntity: CounterEntity): Int

    @Delete
    fun delete(counterEntity: CounterEntity)

    @Query("DELETE FROM counter")
    fun clear()
}
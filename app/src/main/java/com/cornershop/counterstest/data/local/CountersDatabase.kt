package com.cornershop.counterstest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cornershop.counterstest.data.local.CountersDatabase.Companion.CURRENT_DATABASE_VERSION
import com.cornershop.counterstest.domain.local.CounterEntity

@Database(entities = [CounterEntity::class], version = CURRENT_DATABASE_VERSION, exportSchema = true)
abstract class CountersDatabase: RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "counters"
        const val CURRENT_DATABASE_VERSION = 1

        private var db: CountersDatabase? = null

        @Synchronized
        fun getInstance(context: Context) = run {
            if (db == null) {
                db = Room
                    .databaseBuilder(
                        context,
                        CountersDatabase::class.java,
                        DATABASE_NAME
                    )
                    .build()
            }
            db!!
        }
    }

    abstract fun countersDao(): CountersDao
}
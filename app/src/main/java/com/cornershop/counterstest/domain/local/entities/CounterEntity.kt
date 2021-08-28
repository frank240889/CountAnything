package com.cornershop.counterstest.domain.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * A entity that represents a table in the context of Room.
 */
@Entity(tableName = "counter")
data class CounterEntity(
    // next fields represent the columns
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "count")
    var count: Int
){
    // Next fields are not stored as columns
    @Ignore
    var checked: Boolean = false
}

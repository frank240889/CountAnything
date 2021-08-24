package com.cornershop.counterstest.domain.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "counter")
data class CounterEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "count")
    val count: Int
){
    @Ignore
    var checked: Boolean = false
    @Ignore
    var onActionMode: Boolean = false
}

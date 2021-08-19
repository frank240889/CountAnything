package com.cornershop.counterstest.domain.remote

import com.google.gson.annotations.SerializedName

/**
 * Entity to represent a counter object.
 */
data class Counter(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("count")
    val count: Int
    )

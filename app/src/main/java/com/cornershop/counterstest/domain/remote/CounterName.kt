package com.cornershop.counterstest.domain.remote

import com.google.gson.annotations.SerializedName

data class CounterName(@SerializedName("title") val title: String)

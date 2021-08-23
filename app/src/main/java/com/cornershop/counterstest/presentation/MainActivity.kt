package com.cornershop.counterstest.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cornershop.counterstest.R
import com.cornershop.counterstest.presentation.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
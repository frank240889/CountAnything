package com.cornershop.counterstest.presentation.welcome

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.cornershop.counterstest.databinding.ActivityWelcomeBinding
import com.cornershop.counterstest.presentation.BaseViewModelActivity
import com.cornershop.counterstest.presentation.MainActivity

class WelcomeActivity() : BaseViewModelActivity<WelcomeViewModel>() {

    override val viewModel: WelcomeViewModel
        get() = ViewModelProvider(
            this,
            viewModelFactory
        )[WelcomeViewModel::class.java]

    private lateinit var viewBinding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWelcomeBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        if (!viewModel.skipWelcomeScreen()) {
            viewBinding.layoutActivityWelcomeContent.buttonStart.setOnClickListener {
                viewModel.setSkipWelcomeScreen()
                continueToMain()
                finish()
            }
        }
        else {
            continueToMain()
            finish()
        }
    }

    private fun getContinueIntent() = Intent(this, MainActivity::class.java)

    private fun continueToMain() {
        startActivity(getContinueIntent())
    }


}

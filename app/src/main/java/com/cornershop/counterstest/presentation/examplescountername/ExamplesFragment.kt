package com.cornershop.counterstest.presentation.examplescountername

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.cornershop.counterstest.databinding.ExamplesFragmentBinding
import com.cornershop.counterstest.presentation.BaseViewModelFragment

class ExamplesFragment : BaseViewModelFragment<ExamplesViewModel>() {

    companion object {
        fun newInstance() = ExamplesFragment()
    }

    private var _binding: ExamplesFragmentBinding? = null

    private val binding get() = _binding!!

    private val callback: OnBackPressedCallback by lazy {
        provideBackstackCallback()
    }

    private val listener: (String) -> Unit = { name ->

    }

    private val topic1Adapter: ExamplesAdapter by lazy {
        ExamplesAdapter(listener)
    }


    override val viewModel: ExamplesViewModel
        get() = provideViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ExamplesFragmentBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun provideViewModel() = ViewModelProvider(
        this,
        viewModelFactory
    )[ExamplesViewModel::class.java]

    private fun provideBackstackCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

}
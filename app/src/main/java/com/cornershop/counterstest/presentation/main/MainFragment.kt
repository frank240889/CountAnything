package com.cornershop.counterstest.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.databinding.MainFragmentBinding
import com.cornershop.counterstest.domain.local.CounterEntity
import com.cornershop.counterstest.presentation.BaseViewModelFragment
import com.cornershop.counterstest.presentation.createcounter.CreateCounterFragment

class MainFragment : BaseViewModelFragment<MainViewModel>() {

    companion object {
        fun newInstance() = MainFragment()
    }

    override val viewModel: MainViewModel
        get() = ViewModelProvider(
            this,
            viewModelFactory
        )[MainViewModel::class.java]

    private var _binding: MainFragmentBinding? = null

    private val binding get() = _binding!!

    private val countersAdapter: CounterAdapter by lazy {
        CounterAdapter { action, counter ->
            viewModel.performAction(action, counter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
    }

    private fun setupRecyclerView() {
        binding.rvItemCounterList.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = countersAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = MainFragmentBinding.inflate(inflater, container, false).apply {
            _binding = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupListeners()
        viewModel.getCounters()
    }

    private fun setupListeners() {
        binding.apply {
            efabMainFragmentAddCounter.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right
                    )
                    .addToBackStack(CreateCounterFragment::class.java.name)
                    .replace(R.id.container, CreateCounterFragment.newInstance())
                    .commit()
            }
        }
    }

    override fun showLoading(loading: Boolean) {
        binding.pbMainFragmentLoadingIndicator.visibility = if (loading) VISIBLE else GONE
    }

    private fun addObservers() {
        viewModel.observeCounters().observe(this) { state ->
            when (state) {
                is State.Success -> {
                    hideError()
                    showLoading(false)
                    processCounters(state.value)
                }
                is State.Error -> {
                    showLoading(false)
                    showError()
                }
                is State.Loading -> {
                    hideError()
                    showLoading(state.loading)
                }
            }
        }
    }

    private fun processCounters(counters: List<CounterEntity>) {
        if (counters.isEmpty()) {
            showNoCounter()
        }
        else {
            hideNoCounters()
        }
        countersAdapter.submit(counters)
    }

    private fun showError() {
        binding.llMainFragmentNoCountersMessageContainer.visibility = VISIBLE
    }

    private fun hideError() {
        binding.llMainFragmentNoCountersMessageContainer.visibility = GONE
    }

    private fun showNoCounter() {
        binding.llMainFragmentNoCountersMessageContainer.visibility = VISIBLE
    }

    private fun hideNoCounters() {
        binding.llMainFragmentNoCountersMessageContainer.visibility = GONE
    }

}
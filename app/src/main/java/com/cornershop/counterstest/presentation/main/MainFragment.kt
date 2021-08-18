package com.cornershop.counterstest.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.databinding.MainFragmentBinding
import com.cornershop.counterstest.domain.Counter
import dagger.android.support.AndroidSupportInjection

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: MainFragmentBinding? = null

    private val binding = _binding!!

    private val countersAdapter: CounterAdapter by lazy {
        CounterAdapter { action, counter ->
            viewModel.performAction(action, counter)
        }
    }

    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
        setupRecyclerView()
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

    private fun addObservers() {
        viewModel.counters.counters.observe(this) { state ->
            when (state) {
                is State.Success -> {
                    hideError()
                    processCounters(state.value)
                }
                is State.Error -> showError()
                is State.Loading -> {
                    hideError()
                    showLoading(state.loading)
                }
            }
        }
    }

    private fun processCounters(counters: List<Counter>) {
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

    private fun showLoading(loading: Boolean) {
        binding.pbMainFragmentLoadingIndicator.visibility = if (loading) VISIBLE else GONE
    }

}
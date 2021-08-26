package com.cornershop.counterstest.presentation.examplescountername

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.databinding.ExamplesFragmentBinding
import com.cornershop.counterstest.presentation.BaseViewModelFragment
import com.cornershop.counterstest.presentation.createcounter.CreateCounterFragment

/**
 * This screen provides examples for counters names.
 */
class ExamplesFragment : BaseViewModelFragment<ExamplesViewModel>() {

    companion object {
        fun newInstance() = ExamplesFragment()
    }

    /**
     * Next components provide UI functionality
     */
    private var _binding: ExamplesFragmentBinding? = null

    private val binding get() = _binding!!

    private val listener: (String) -> Unit = { name ->
        goBackWithSelectedName(name)
    }

    private val topic1Adapter: ExamplesAdapter by lazy {
        ExamplesAdapter(listener)
    }

    private val topic2Adapter: ExamplesAdapter by lazy {
        ExamplesAdapter(listener)
    }

    private val topic3Adapter: ExamplesAdapter by lazy {
        ExamplesAdapter(listener)
    }



    override val viewModel: ExamplesViewModel
        get() = provideViewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservables()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ExamplesFragmentBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclersView()
        setupListeners()
    }

    private fun setupListeners() {
        binding.ibExamplesFragmentBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclersView() {
        binding.apply {
            rvExamplesFragmentTopic1.apply {
                adapter = topic1Adapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.HORIZONTAL,
                    false
                )
            }

            rvExamplesFragmentTopic2.apply {
                adapter = topic2Adapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.HORIZONTAL,
                    false
                )
            }

            rvExamplesFragmentTopic3.apply {
                adapter = topic3Adapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.HORIZONTAL,
                    false
                )
            }
        }
    }

    private fun setupObservables() {
        viewModel.apply {
            topic1.observe(this@ExamplesFragment) { data ->
                setHeader(data.removeAt(0), binding.tvExamplesFragmentTopic1)
                topic1Adapter.submit(data)
            }

            topic2.observe(this@ExamplesFragment) { data ->
                setHeader(data.removeAt(0), binding.tvExamplesFragmentTopic2)
                topic2Adapter.submit(data)

            }

            topic3.observe(this@ExamplesFragment) { data ->
                setHeader(data.removeAt(0), binding.tvExamplesFragmentTopic3)
                topic3Adapter.submit(data)
            }
        }
    }

    private fun setHeader(title: String, tvHeader: TextView) {
        tvHeader.text = title
    }

    override fun provideViewModel() = ViewModelProvider(
        this,
        viewModelFactory
    )[ExamplesViewModel::class.java]

    private fun goBackWithSelectedName(name: String) {
        parentFragmentManager.setFragmentResult(
            CreateCounterFragment.NAME, // Same request key FragmentA used to register its listener
            bundleOf(CreateCounterFragment.NAME to name) // The data to be passed to FragmentA
        )
        parentFragmentManager.popBackStack()
    }
}
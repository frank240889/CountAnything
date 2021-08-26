package com.cornershop.counterstest.presentation.examplescountername

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onDetach() {
        super.onDetach()
        callback.remove()
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

    private fun provideBackstackCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    private fun goBackWithSelectedName(name: String) {
        parentFragmentManager.setFragmentResult(
            "name", // Same request key FragmentA used to register its listener
            bundleOf("name" to name) // The data to be passed to FragmentA
        )
        parentFragmentManager.popBackStack()
    }
}
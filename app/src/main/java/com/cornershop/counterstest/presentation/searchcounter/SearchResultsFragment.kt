package com.cornershop.counterstest.presentation.searchcounter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.common.ViewModelFactory
import com.cornershop.counterstest.databinding.SearchResultFragmentBinding
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.presentation.common.CounterAdapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SearchResultsFragment : DialogFragment(), HasAndroidInjector {

    companion object {
        fun newInstance() = SearchResultsFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Any>


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private val viewModel: SearchViewModel
        get() = provideViewModel()


    private var _binding: SearchResultFragmentBinding? = null

    private val binding get() = _binding!!

    private val countersAdapter: CounterAdapter by lazy {
        CounterAdapter (
            { action, counter ->
                viewModel.performAction(action, counter)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
        addObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = run {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        SearchResultFragmentBinding.inflate(inflater, container, false).apply {
            _binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setFullScreenWindow()
        setupRecyclerView()
        setupListeners()
        binding.mtSearchResultFragmentToolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        (viewModel.observeCounters().value as? State.Success<List<CounterEntity>>)?.let { state ->
            processCounters(state.value)
            setHeaderText(state.value)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun androidInjector(): AndroidInjector<Any> = injector

    private fun setFullScreenWindow() {
        requireDialog().apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            window?.setLayout(width, height)
        }
    }

    private fun provideViewModel() =  ViewModelProvider(
        this,
        viewModelFactory
    )[SearchViewModel::class.java]

    private fun setupRecyclerView() {
        binding.rvSearchResultItemCounterList.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = countersAdapter
        }
    }

    private fun setupListeners() {
        binding.tietSearchResultFragmentSearchInput.doOnTextChanged { text, _, _, _ ->
            binding.apply {
                if (clSearchResultFragmentContainerResults.alpha == 0f) {
                    clSearchResultFragmentContainerResults.animate()
                        .setDuration(100)
                        .alpha(1f)
                        .withStartAction {
                            clSearchResultFragmentContainerResults.background = ColorDrawable(Color.WHITE)
                            clSearchResultFragmentContainerResults.visibility = VISIBLE
                        }
                }
                viewModel.search(text.toString())
            }
        }
    }

    private fun addObservers() {

        viewModel.apply {
            observeSearchResults().observe(this@SearchResultsFragment) { data ->
                processCounters(data)
                setHeaderText(data)
            }

            observeIncrementCounter().observe(this@SearchResultsFragment) { state ->
                when (state) {
                    is State.Success<*> -> {
                        // NO OP
                    }
                    is State.Error -> {

                    }
                    is State.Loading -> {
                        // NO OP
                    }
                }
            }

            observeDecrementCounter().observe(this@SearchResultsFragment) { state ->
                when (state) {
                    is State.Success<*> -> {
                        // NO OP
                    }
                    is State.Error -> {

                    }
                    is State.Loading -> {
                        // NO OP
                    }
                }
            }
        }
    }

    private fun setHeaderText(value: List<CounterEntity>) {
        binding.tvSearchResultHeader.text = if (value.isEmpty()) {
            ""
        }
        else {
            val items = SpannableString(getString(R.string.n_items, value.size)).apply {
                setSpan(StyleSpan(Typeface.BOLD),0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val times = getString(R.string.n_times, value.sumOf { it.count})
            "$items $times"
        }
    }

    private fun processCounters(counters: List<CounterEntity>) {
        if (counters.isEmpty()) {
            showResultMessage(VISIBLE)
        }
        else {
            showResultMessage(GONE)
        }
        countersAdapter.submit(counters)
    }

    private fun showResultMessage(visibility: Int) {
        binding.tvSearchResultTitleNoCounters.visibility = visibility
    }
}
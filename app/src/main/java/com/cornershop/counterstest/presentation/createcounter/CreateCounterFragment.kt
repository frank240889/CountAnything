package com.cornershop.counterstest.presentation.createcounter

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.databinding.CreateCounterFragmentBinding
import com.cornershop.counterstest.presentation.BaseViewModelFragment
import com.cornershop.counterstest.presentation.dialogs.InformativeDialogFragment
import com.cornershop.counterstest.presentation.examplescountername.ExamplesFragment

class CreateCounterFragment : BaseViewModelFragment<CreateCounterViewModel>() {

    companion object {
        fun newInstance() = CreateCounterFragment()
    }

    override val viewModel: CreateCounterViewModel by lazy {
        provideViewModel()
    }

    private var _viewBinding: CreateCounterFragmentBinding? = null

    private val viewBinding get() = _viewBinding!!

    private val callback: OnBackPressedCallback by lazy {
        provideBackstackCallback()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        setupObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = CreateCounterFragmentBinding.inflate(inflater, container, false).apply {
        _viewBinding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setStyleSuggest()
        parentFragmentManager.setFragmentResultListener(
            "name",
            viewLifecycleOwner,
            { _, bundle ->
                viewBinding
                    .tietCreateCounterFragmentCounterName
                    .setText(bundle.getString("name").toString())
            }
        )
    }

    private fun setStyleSuggest() {
        val message = SpannableString(getString(R.string.create_counter_disclaimer)).apply {
            setSpan(object: ClickableSpan() {
                override fun onClick(widget: View) {
                    parentFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right, R.anim.slide_out_left,
                            R.anim.slide_in_left, R.anim.slide_out_right
                        )
                        .addToBackStack(ExamplesFragment::class.java.name)
                        .replace(
                            R.id.container,
                            ExamplesFragment.newInstance(),
                            ExamplesFragment::class.java.name
                        )
                        .commit()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            },length - 8, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        viewBinding.tvCreateCounterFragmentSuggestCounterName.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = message
        }
    }

    private fun setupListeners() {
        viewBinding.apply {
            ibCreateCounterFragmentClose.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            mbCreateCounterFragmentSaveCounter.setOnClickListener {
                viewModel.createCounter()
            }

            tietCreateCounterFragmentCounterName.doOnTextChanged { text, _, _, _ ->
                viewModel.setTitle(text.toString())
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun showLoading(loading: Boolean) {
        viewBinding.apply {
            if (loading) {
                pbCreateCounterFragmentLoadingIndicator.visibility = VISIBLE
                vCreateCounterFragmentBlockingLayer.visibility = VISIBLE
                mbCreateCounterFragmentSaveCounter.visibility = GONE
            }
            else {
                pbCreateCounterFragmentLoadingIndicator.visibility = GONE
                vCreateCounterFragmentBlockingLayer.visibility = GONE
                mbCreateCounterFragmentSaveCounter.visibility = VISIBLE
            }
        }

    }

    override fun provideViewModel() = ViewModelProvider(
        this,
        viewModelFactory
    )[CreateCounterViewModel::class.java]

    private fun provideBackstackCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.observeCounterCreation().observe(this) { state ->
            when (state) {
                is State.Success -> {
                    showLoading(false)
                    parentFragmentManager.popBackStack()
                }
                is State.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
                is State.Loading -> {
                    showLoading(state.loading)
                }
            }
        }

    }

    private fun showError(message: String) {
        InformativeDialogFragment.newInstance(
            title = getString(R.string.error_creating_counter_title),
            message = message,
            positiveButtonText = getString(R.string.ok)
        ).show(childFragmentManager, InformativeDialogFragment::class.java.name)
    }
}

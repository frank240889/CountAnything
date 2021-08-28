package com.cornershop.counterstest.presentation.createcounter

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.databinding.CreateCounterFragmentBinding
import com.cornershop.counterstest.presentation.BaseViewModelFragment
import com.cornershop.counterstest.presentation.dialogs.InformativeDialogFragment
import com.cornershop.counterstest.presentation.examplescountername.ExamplesFragment

/**
 * The screen to create new counters.
 */
class CreateCounterFragment : BaseViewModelFragment<CreateCounterViewModel>() {

    companion object {
        const val NAME = "name"
        fun newInstance() = CreateCounterFragment()
    }

    override val viewModel: CreateCounterViewModel by lazy {
        provideViewModel()
    }

    private var _viewBinding: CreateCounterFragmentBinding? = null

    private val viewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        addFragmentCallback()
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

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }

    /**
     * Instead of using custom interfaces as callback to pass data between fragments, we use
     * the way provided by the Android framework. Automatically will be unregistered when fragment
     * lifecycle exits from on resume.
     */
    private fun addFragmentCallback() {
        parentFragmentManager.setFragmentResultListener(
            NAME,
            viewLifecycleOwner,
            { _, bundle ->
                viewBinding
                    .tietCreateCounterFragmentCounterName
                    .setText(bundle.getString(NAME).toString())
            }
        )
    }

    /**
     * Set the style for the text "see examples."
     */
    private fun setStyleSuggest() {
        val message = SpannableString(getString(R.string.create_counter_disclaimer)).apply {
            setSpan(object: ClickableSpan() {
                override fun onClick(widget: View) {
                    goToExamplesScreen()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            },length - 9, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        viewBinding.tvCreateCounterFragmentSuggestCounterName.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = message
        }
    }

    private fun goToExamplesScreen() {
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

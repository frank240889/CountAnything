package com.cornershop.counterstest.presentation.main

import android.os.Bundle
import android.view.*
import android.view.View.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.databinding.MainFragmentBinding
import com.cornershop.counterstest.domain.local.CounterEntity
import com.cornershop.counterstest.presentation.BaseViewModelFragment
import com.cornershop.counterstest.presentation.createcounter.CreateCounterFragment
import com.cornershop.counterstest.presentation.dialogs.InformativeDialogFragment

class MainFragment : BaseViewModelFragment<MainViewModel>() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var actionMode: ActionMode? = null
    override val viewModel: MainViewModel
        get() = ViewModelProvider(
            this,
            viewModelFactory
        )[MainViewModel::class.java]

    private var _binding: MainFragmentBinding? = null

    private val binding get() = _binding!!

    private val countersAdapter: CounterAdapter by lazy {
        CounterAdapter (
            { action, counter ->
                viewModel.performAction(action, counter)
            },
            { action, counter ->
                requireActivity().startActionMode(actionModeCallback)
                viewModel.performAction(action, counter)
                counter.checked = counter.checked.not()
            }
        )
    }


    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.main_menu, menu)
            setActionModeOn()
            return true
        }
        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false // Return false if nothing is done

        // Called when the user selects a contextual menu item
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem) =
            handleActionMode(mode, item)

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) = setActionModeOff()
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
        viewModel.apply {
            counters.observe(this@MainFragment) { state ->
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

            observeDeletionCounters().observe(this@MainFragment) { state ->
                when (state) {
                    is State.Success<*> -> {
                        showLoading(false)
                    }
                    is State.Error -> {
                        showLoading(false)
                        showDeletionError(state.message)
                    }
                    is State.Loading -> {
                        showLoading(state.loading)
                    }
                }
            }
        }
    }

    private fun showDeletionError(message: String) {
        InformativeDialogFragment
            .newInstance(
                title = getString(R.string.error_deleting_counter_title),
                message = message,
                positiveButtonText = getString(R.string.ok)
            )
            .show(childFragmentManager, InformativeDialogFragment::class.java.name)
    }

    private fun processCounters(counters: List<CounterEntity>) {
        if (counters.isEmpty()) {
            actionMode?.finish()
            showNoCounterMessage()
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

    private fun showNoCounterMessage() {
        binding.llMainFragmentNoCountersMessageContainer.visibility = VISIBLE
    }

    private fun hideNoCounters() {
        binding.llMainFragmentNoCountersMessageContainer.visibility = GONE
    }

    private fun confirmDelete() {
        viewModel.getCountersName().let { names ->

            InformativeDialogFragment
                .newInstance(
                    message = getString(R.string.delete_x_question, names),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.cancel)
                ).apply {
                    setCallback(object : InformativeDialogFragment.OnButtonPressed {
                        override fun onPositive() {
                            viewModel.delete()
                        }

                        override fun onNegative() {
                            actionMode?.finish()
                        }
                    })
                }
                .show(childFragmentManager, InformativeDialogFragment::class.java.name)
        }
    }

    private fun setActionModeOn() {
        countersAdapter.onActionMode(true)
        binding.apply {
            mcvMainFragmentSearchContainer.visibility = INVISIBLE
            efabMainFragmentAddCounter.hide()
        }
    }

    private fun setActionModeOff() {
        actionMode = null
        countersAdapter.onActionMode(false)
        binding.apply {
            mcvMainFragmentSearchContainer.visibility = VISIBLE
            efabMainFragmentAddCounter.show()
        }
        viewModel.clearDeletionList()
    }

    private fun handleActionMode(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_share -> {
                //shareCurrentItem()
                mode.finish() // Action picked, so close the CAB
                true
            }
            R.id.menu_action_delete -> {
                confirmDelete()
                false
            }
            else -> false
        }
    }
}
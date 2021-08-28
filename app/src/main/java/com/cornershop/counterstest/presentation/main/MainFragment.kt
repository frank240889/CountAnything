package com.cornershop.counterstest.presentation.main

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.*
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.common.ShareHelper
import com.cornershop.counterstest.common.State
import com.cornershop.counterstest.common.Utils.TEXT_PLAIN
import com.cornershop.counterstest.databinding.MainFragmentBinding
import com.cornershop.counterstest.domain.local.entities.CounterEntity
import com.cornershop.counterstest.presentation.BaseViewModelFragment
import com.cornershop.counterstest.presentation.common.Action
import com.cornershop.counterstest.presentation.common.CounterAdapter
import com.cornershop.counterstest.presentation.createcounter.CreateCounterFragment
import com.cornershop.counterstest.presentation.dialogs.InformativeDialogFragment
import com.cornershop.counterstest.presentation.searchcounter.SearchResultsFragment
import javax.inject.Inject

/**
 * The main screen shows the counters list or messages depending on data empty or error
 * while fetching it.
 */
class MainFragment : BaseViewModelFragment<MainViewModel>() {

    companion object {
        fun newInstance() = MainFragment()
    }

    /**
     * Helper object to share feature.
     */
    @Inject
    lateinit var shareHelper: ShareHelper

    override val viewModel: MainViewModel
        get() = provideViewModel()

    private var actionMode: ActionMode? = null


    private var _binding: MainFragmentBinding? = null

    private val binding get() = _binding!!

    private val countersAdapter: CounterAdapter by lazy {
        CounterAdapter (
            { action, counter ->
                viewModel.performAction(action, counter)
                actionMode?.title = getString(R.string.n_selected, viewModel.counterAmount())
            },
            { action, counter ->
                handleOnLongClick(action, counter)
            }
        )
    }

    /**
     * The callback when action mode is enabled.
     */
    private val actionModeCallback: ActionMode.Callback by lazy {
        object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate a menu resource providing context menu items
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.main_menu, menu)
                setActionModeOn(mode)
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
        viewModel.fetchCounters()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = MainFragmentBinding.inflate(inflater, container, false).apply {
            _binding = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRefreshLayout()
        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateHeader()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun showLoading(loading: Boolean) {
        binding.apply {
            pbMainFragmentLoadingIndicator.visibility = if (loading) VISIBLE else GONE
            srlItemCounterRefresh.isRefreshing = loading
        }
    }

    override fun provideViewModel() =  ViewModelProvider(
        this,
        viewModelFactory
    )[MainViewModel::class.java]

    private fun updateHeader() {
        (viewModel.observeCounters().value as? State.Success<List<CounterEntity>>)?.let { state ->
            processCounters(state.value)
            setHeaderText(state.value)
        }
    }

    private fun setupRefreshLayout() {
        binding.srlItemCounterRefresh.apply {
            setOnRefreshListener {
                viewModel.fetchCounters(true)
            }
            setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
        }
    }

    private fun setupRecyclerView() {
        binding.incMainFragmentCounterList.rvItemCounterList.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = countersAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            efabMainFragmentAddCounter.setOnClickListener {
                goToCreateFragment()
            }

            mbMainFragmentErrorRetry.setOnClickListener {
                viewModel.fetchCounters(true)
            }
            mcvMainFragmentSearchContainer.setOnClickListener {
                goToSearch()
            }
        }
    }

    private fun goToSearch() {
        SearchResultsFragment.newInstance().show(childFragmentManager, SearchResultsFragment::class.java.name )
    }

    private fun goToCreateFragment() {
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

    private fun addObservers() {
        viewModel.apply {
            observeCounters().observe(this@MainFragment) { state ->
                when (state) {
                    is State.Success -> {
                        showErrorMessage(GONE)
                        showLoading(false)
                        processCounters(state.value)
                        setHeaderText(state.value)
                    }
                    is State.Error -> {
                        showLoading(false)
                        showErrorMessage(VISIBLE)
                        showNoCountersMessage(GONE)
                    }
                    is State.Loading -> {
                        showNoCountersMessage(GONE)
                        showErrorMessage(GONE)
                        showLoading(state.loading)
                    }
                }
            }

            observeDeletionCounters().observe(this@MainFragment) { state ->
                when (state) {
                    is State.Success<*> -> {
                        showLoading(false)
                        actionMode?.title = getString(R.string.n_selected, viewModel.counterAmount())
                    }
                    is State.Error -> {
                        showLoading(false)
                        showDialog(
                            title = getString(R.string.error_deleting_counter_title),
                            message = state.message,
                            positiveButtonText = getString(R.string.ok)
                        )
                    }
                    is State.Loading -> {
                        showLoading(state.loading)
                    }
                }
            }

            observeIncrementCounter().observe(this@MainFragment) { state ->
                when (state) {
                    is State.Success<*> -> {
                        // NO OP
                    }
                    is State.Error -> {
                        showDialog(
                            title = getString(
                                R.string.error_updating_counter_title,
                                viewModel.currentIncrementCounter().title,
                                viewModel.currentIncrementCounter().count + 1
                            ),
                            message = state.message,
                            positiveButtonText = getString(R.string.retry),
                            negativeButtonText = getString(R.string.dismiss),
                            onButtonPressed = object: InformativeDialogFragment.OnButtonPressed {
                                override fun onPositive() {
                                    viewModel.performAction(
                                        Action.INCREMENT,
                                        viewModel.currentIncrementCounter()
                                    )
                                }
                            }
                        )
                    }
                    is State.Loading -> {
                        // NO OP
                    }
                }
            }

            observeDecrementCounter().observe(this@MainFragment) { state ->
                when (state) {
                    is State.Success<*> -> {
                        // NO OP
                    }
                    is State.Error -> {
                        showDialog(
                            title = getString(
                                R.string.error_updating_counter_title,
                                viewModel.currentDecrementCounter().title,
                                viewModel.currentDecrementCounter().count - 1
                            ),
                            message = state.message,
                            positiveButtonText = getString(R.string.retry),
                            negativeButtonText = getString(R.string.dismiss),
                            onButtonPressed = object: InformativeDialogFragment.OnButtonPressed {
                                override fun onPositive() {
                                    viewModel.performAction(
                                        Action.DECREMENT,
                                        viewModel.currentDecrementCounter()
                                    )
                                }
                            }
                        )
                    }
                    is State.Loading -> {
                        // NO OP
                    }
                }
            }

            observeShareCounter().observe(this@MainFragment) { state ->
                (state as? State.Success<String>)?.let { content ->
                    shareHelper.share(content.value, TEXT_PLAIN, requireActivity())
                }
            }
        }
    }

    /**
     * Updates the header text.
     */
    private fun setHeaderText(value: List<CounterEntity>) {
        binding.tvMainFragmentHeader.text = if (value.isEmpty()) {
            ""
        }
        else {
            val items = SpannableString(getString(R.string.n_items, value.size)).apply {
                setSpan(StyleSpan(Typeface.BOLD),0, this.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val times = getString(R.string.n_times, value.sumOf { it.count})
            "$items $times"
        }
    }

    private fun showDialog(
        title: String = "",
        message: String,
        positiveButtonText: String = "",
        negativeButtonText: String = "",
        onButtonPressed: InformativeDialogFragment.OnButtonPressed? = null
    ) {
        InformativeDialogFragment
            .newInstance(
                title = title,
                message = message,
                positiveButtonText = positiveButtonText,
                negativeButtonText = negativeButtonText
            ).apply {
                onButtonPressed?.let { callback ->
                    setCallback(callback)
                }
            }
            .show(childFragmentManager, InformativeDialogFragment::class.java.name)
    }

    private fun processCounters(counters: List<CounterEntity>) {
        if (counters.isEmpty()) {
            actionMode?.finish()
            showNoCountersMessage(VISIBLE)
        }
        else {
            showNoCountersMessage(GONE)
        }
        countersAdapter.submit(counters)
    }

    private fun showNoCountersMessage(visibility: Int) {
        binding.llMainFragmentNoCountersMessageContainer.visibility = visibility
    }

    private fun showErrorMessage(visibility: Int) {
        binding.llMainFragmentErrorMessageContainer.visibility = visibility
    }

    private fun confirmDelete() {
        if (viewModel.countersCanBeDeleted()) {
            showDialog(
                message = getString(R.string.delete_x_question, viewModel.getCountersName()),
                positiveButtonText = getString(R.string.delete),
                negativeButtonText = getString(R.string.cancel),
                onButtonPressed = object: InformativeDialogFragment.OnButtonPressed {
                    override fun onPositive() {
                        viewModel.delete()
                    }

                    override fun onNegative() {
                        actionMode?.finish()
                    }
                }
            )
        }
        else {
            showDialog(
                title = getString(R.string.error_deleting_counter_title),
                message = getString(R.string.no_items_to_delete),
                positiveButtonText = getString(R.string.ok)
            )
        }
    }

    private fun setActionModeOn(actionMode: ActionMode?) {
        this.actionMode = actionMode
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
        viewModel.clearMultiselect()
    }

    private fun handleActionMode(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_share -> {
                handleShareAction(mode)
                true
            }
            R.id.menu_action_delete -> {
                confirmDelete()
                false
            }
            else -> false
        }
    }

    private fun handleShareAction(mode: ActionMode) {
        if (viewModel.countersCanBeShared()) {
            viewModel.share()
            mode.finish()
        }
        else {
            showDialog(
                title = getString(R.string.error_sharing_counter_title),
                message = getString(R.string.no_share_items_selected),
                positiveButtonText = getString(R.string.ok)
            )
        }
    }

    private fun handleOnLongClick(action: Action, counter: CounterEntity) {
        requireActivity().startActionMode(actionModeCallback)
        viewModel.performAction(action, counter)
        counter.checked = counter.checked.not()
        actionMode?.title = getString(R.string.n_selected, viewModel.counterAmount())
    }
}
package com.cornershop.counterstest.presentation.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.domain.local.entities.CounterEntity

class CounterAdapter(
    private val onClickListener: (Action, CounterEntity) -> Unit,
    private val onLongClickListener: ((Action, CounterEntity) -> Unit)? = null

): RecyclerView.Adapter<CounterAdapter.CounterViewHolder>() {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CounterEntity>() {
            override fun areItemsTheSame(oldItem: CounterEntity, newItem: CounterEntity) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CounterEntity, newItem: CounterEntity) =
                oldItem.count == newItem.count

            override fun getChangePayload(oldItem: CounterEntity, newItem: CounterEntity): Any? {
                return if (newItem.id == oldItem.id) {
                    Bundle().apply {
                        putString("count", newItem.count.toString())
                    }
                }
                else {
                    super.getChangePayload(oldItem, newItem)
                }
            }
        }

        enum class Action {
            INCREMENT,
            DECREMENT,
            MULTISELECT
        }
    }

    private var actionMode: Boolean = false
    private val diffUtil: AsyncListDiffer<CounterEntity> = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CounterViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_counter, parent, false)
        )

    override fun onBindViewHolder(holder: CounterViewHolder, position: Int) {
        onBindViewHolder(holder, position, ArrayList())
    }

    override fun onBindViewHolder(
        holder: CounterViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        processPayload(holder, position, payloads)
    }

    override fun getItemCount() = diffUtil.currentList.size

    fun submit(counters: List<CounterEntity>) = diffUtil.submitList(counters)

    private fun processPayload(holder: CounterViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            diffUtil.currentList[position].let { item ->
                holder.bind(item)
            }
        }
        else {
            (payloads[0] as? Bundle)?.let { bundle ->
                holder.update(bundle)
            }
        }
    }

    fun onActionMode(actionMode: Boolean) {
        this.actionMode = actionMode
        diffUtil.currentList.forEachIndexed { index, counterEntity ->
            if (!actionMode) {
                counterEntity.checked = actionMode
            }
            notifyItemChanged(index)
        }
    }


    inner class CounterViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_item_counter_title_counter)
        private val tvCounter: TextView = itemView.findViewById(R.id.tv_item_counter_counter_marker)
        private val ibIncrement: ImageButton = itemView.findViewById(R.id.ib_item_counter_increment_counter)
        private val ibDecrement: ImageButton = itemView.findViewById(R.id.ib_item_counter_decrement_counter)
        private val llButtonsContainer: LinearLayout = itemView.findViewById(R.id.ll_item_counter_buttons_counter_container)
        private val flSelectedLayer: FrameLayout = itemView.findViewById(R.id.fl_item_counter_selected_layer)

        init {
            ibDecrement.setOnClickListener(this)
            ibIncrement.setOnClickListener(this)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(counter: CounterEntity) {
            tvTitle.text = counter.title
            tvCounter.text = counter.count.toString()

            if (actionMode) {
                llButtonsContainer.visibility = INVISIBLE
                    if (counter.checked) {
                        flSelectedLayer.visibility = VISIBLE
                    } else {
                        flSelectedLayer.visibility = INVISIBLE
                    }
            }
            else {
                llButtonsContainer.visibility = VISIBLE
                flSelectedLayer.visibility = INVISIBLE
            }
        }

        override fun onClick(v: View?) {
            if (actionMode) {
                diffUtil.currentList[adapterPosition].let {
                    it.checked = it.checked.not()
                    notifyItemChanged(adapterPosition)
                }
                onClickListener.invoke(Action.MULTISELECT, diffUtil.currentList[adapterPosition])
            }
            else {
                when (v?.id) {
                    R.id.ib_item_counter_increment_counter -> onClickListener.invoke(Action.INCREMENT, diffUtil.currentList[adapterPosition])
                    R.id.ib_item_counter_decrement_counter -> onClickListener.invoke(Action.DECREMENT, diffUtil.currentList[adapterPosition])
                    else -> Log.d(CounterAdapter::class.java.name, "Cannot handle click for view ${v?.id}")
                }
            }
        }

        fun update(bundle: Bundle) {
            bundle["count"]?.let { count ->
                tvCounter.text = count.toString()
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (!actionMode) {
                onLongClickListener?.invoke(Action.MULTISELECT, diffUtil.currentList[adapterPosition])
                notifyItemChanged(adapterPosition)
            }
            return true
        }
    }
}
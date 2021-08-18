package com.cornershop.counterstest.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.domain.Counter

class CounterAdapter(
    private val listener: (Action, Counter) -> Unit
): RecyclerView.Adapter<CounterAdapter.CounterViewHolder>() {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Counter>() {
            override fun areItemsTheSame(oldItem: Counter, newItem: Counter) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Counter, newItem: Counter) =
                oldItem.value == newItem.value

            override fun getChangePayload(oldItem: Counter, newItem: Counter): Any? {
                return if (newItem.id == oldItem.id) {
                    Bundle().apply {
                        putString("count", newItem.value.toString())
                    }
                }
                else {
                    super.getChangePayload(oldItem, newItem)
                }
            }
        }

        enum class Action {
            INCREMENT,
            DECREMENT
        }
    }

    private val items: MutableList<Counter>  by lazy {
        ArrayList()
    }

    private val diffUtil: AsyncListDiffer<Counter> = AsyncListDiffer(this, DIFF_CALLBACK)

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

    fun submit(counters: List<Counter>?) = diffUtil.submitList(counters)

    private fun processPayload(holder: CounterViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            items[position].let { item ->
                holder.bind(item)
            }
        }
        else {
            (payloads[0] as? Bundle)?.let { bundle ->
                holder.update(bundle)
            }
        }
    }

    inner class CounterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_item_counter_title_counter)
        private val tvCounter: TextView = itemView.findViewById(R.id.tv_item_counter_counter_marker)
        private val ibIncrement: ImageButton = itemView.findViewById(R.id.ib_item_counter_increment_counter)
        private val ibDecrement: ImageButton = itemView.findViewById(R.id.ib_item_counter_decrement_counter)

        init {
            ibDecrement.setOnClickListener(this)
            ibIncrement.setOnClickListener(this)
        }

        fun bind(counter: Counter) {
            tvTitle.text = counter.name
            tvCounter.text = counter.value.toString()
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.ib_item_counter_increment_counter -> listener.invoke(Action.INCREMENT, items[adapterPosition])
                R.id.ib_item_counter_decrement_counter -> listener.invoke(Action.DECREMENT, items[adapterPosition])
            }
        }

        fun update(bundle: Bundle) {
            bundle["count"]?.let { count ->
                tvCounter.text = count.toString()
            }
        }
    }
}
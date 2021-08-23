package com.cornershop.counterstest.presentation.examplescountername

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.cornershop.counterstest.R
import com.google.android.material.chip.Chip

class ExamplesAdapter(
    private val listener: (String) -> Unit
): Adapter<ExamplesAdapter.ExamplesViewHolder>() {
    private val names = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExamplesViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_example, parent, false)
        )

    override fun onBindViewHolder(holder: ExamplesViewHolder, position: Int) {
        holder.bind(names[position])
    }

    override fun getItemCount() = names.size

    fun submit(names: List<String>) {
        this.names.apply {
            clear()
            addAll(names)
        }
        notifyDataSetChanged()
    }

    inner class ExamplesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val cExampleName: Chip = itemView.findViewById(R.id.c_item_example_name)
        init {
            cExampleName.setOnClickListener {
                listener.invoke(names[adapterPosition])
            }
        }

        fun bind(name: String) {
            cExampleName.text = name
        }
    }
}
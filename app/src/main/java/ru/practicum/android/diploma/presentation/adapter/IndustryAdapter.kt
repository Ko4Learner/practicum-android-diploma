package ru.practicum.android.diploma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Industry

class IndustryAdapter(
    private val onItemClick: (Int?, Boolean) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {
    var industries = emptyList<Industry>()
    private var selectedItem: Int? = null

    inner class IndustryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.industryName)
        fun bind(model: Industry) {
            text.text = model.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        return IndustryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.industry_item_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return industries.size
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        holder.bind(industries[position])
        val radioButton = holder.itemView.findViewById<RadioButton>(R.id.industryRadioButton)

        radioButton.setOnClickListener {
            val isSame = holder.adapterPosition != selectedItem
            radioButton.isChecked = isSame
            onItemClick(selectedItem, isSame)
            selectedItem = if (isSame) {
                holder.adapterPosition
            } else {
                null
            }
        }

        radioButton.isChecked = position == selectedItem
    }

    fun clear() {
        selectedItem = null
        industries = emptyList()
    }

    fun getSelectedItemId(): Industry? {
        return if (selectedItem != null) industries[selectedItem!!] else null
    }
}

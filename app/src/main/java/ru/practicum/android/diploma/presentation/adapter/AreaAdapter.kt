package ru.practicum.android.diploma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Area

class AreaAdapter : RecyclerView.Adapter<AreaAdapter.AreaViewHolder>() {


    private var areaList = mutableListOf<Area>()

    fun updateItems(item: List<Area>) {
        areaList = item.toMutableList()
        notifyDataSetChanged()
    }

    var onItemClick: ((String) -> Unit)? = null

    inner class AreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.textCard)
        fun bind(model: Area) {
            text.text = model.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        return AreaViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.filter_item_region_and_country, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.bind(areaList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.let { it1 -> it1(areaList[position].name) }
        }
    }

    override fun getItemCount(): Int = areaList.size
}

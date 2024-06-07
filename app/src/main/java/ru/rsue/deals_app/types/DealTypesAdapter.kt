package ru.rsue.deals_app.types

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rsue.deals_app.R

class DealTypesAdapter(
    private val dealTypes: MutableList<DealType>,
    private val onDeleteClick: (DealType) -> Unit,
    private val onEditClick: (DealType) -> Unit,
    private val onItemClick: (DealType) -> Unit
) : RecyclerView.Adapter<DealTypesAdapter.DealTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deal_type, parent, false)
        return DealTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealTypeViewHolder, position: Int) {
        val dealType = dealTypes[position]
        holder.bind(dealType)
    }

    override fun getItemCount(): Int {
        return dealTypes.size
    }

    inner class DealTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dealTypeTextView: TextView = itemView.findViewById(R.id.tvDealType)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)

        fun bind(dealType: DealType) {
            dealTypeTextView.text = dealType.type
            deleteButton.setOnClickListener {
                onDeleteClick(dealType)
            }
            editButton.setOnClickListener {
                onEditClick(dealType)
            }
            itemView.setOnClickListener {
                onItemClick(dealType)
            }
        }
    }

}



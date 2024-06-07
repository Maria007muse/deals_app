package ru.rsue.deals_app.deals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rsue.deals_app.R


class DealsAdapter(
    private val deals: MutableList<Deal>,
    private val onDeleteClick: (Deal) -> Unit,
    private val onEditClick: (Deal) -> Unit,
    private val onItemClick: (Deal) -> Unit,
    ) : RecyclerView.Adapter<DealsAdapter.DealViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deal, parent, false)
        return DealViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals[position]
        holder.bind(deal)
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dealTextView: TextView = itemView.findViewById(R.id.tvDeal)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)

        fun bind(deal: Deal) {
            dealTextView.text = "Сделка №${deal.id}"
            deleteButton.setOnClickListener {
                onDeleteClick(deal)
            }
            editButton.setOnClickListener {
                onEditClick(deal)
            }
            itemView.setOnClickListener {
                onItemClick(deal)
            }
        }
    }

}

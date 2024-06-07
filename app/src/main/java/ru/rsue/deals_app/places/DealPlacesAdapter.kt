package ru.rsue.deals_app.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rsue.deals_app.R


class DealPlacesAdapter(
    private val dealPlaces: MutableList<DealPlace>,
    private val onDeleteClick: (DealPlace) -> Unit,
    private val onEditClick: (DealPlace) -> Unit,
    private val onItemClick: (DealPlace) -> Unit
) : RecyclerView.Adapter<DealPlacesAdapter.DealPlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealPlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deal_place, parent, false)
        return DealPlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealPlaceViewHolder, position: Int) {
        val dealPlace = dealPlaces[position]
        holder.bind(dealPlace)
    }

    override fun getItemCount(): Int {
        return dealPlaces.size
    }

    inner class DealPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dealPlaceFullTextView: TextView = itemView.findViewById(R.id.tvDealPlaceFull)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)

        fun bind(dealPlace: DealPlace) {
            dealPlaceFullTextView.text = dealPlace.deal_place_full
            deleteButton.setOnClickListener {
                onDeleteClick(dealPlace)
            }
            editButton.setOnClickListener {
                onEditClick(dealPlace)
            }
            itemView.setOnClickListener {
                onItemClick(dealPlace)
            }
        }
    }

}

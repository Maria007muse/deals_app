package ru.rsue.deals_app.currencies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rsue.deals_app.R

class CurrenciesAdapter(
    private val currencies: MutableList<Currency>,
    private val onDeleteClick: (Currency) -> Unit,
    private val onEditClick: (Currency) -> Unit,
    private val onItemClick: (Currency) -> Unit
) : RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencies[position]
        holder.bind(currency)
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currencyShortTextView: TextView = itemView.findViewById(R.id.tvCurrencyShort)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)

        fun bind(currency: Currency) {
            currencyShortTextView.text = currency.currency_short
            deleteButton.setOnClickListener {
                onDeleteClick(currency)
            }
            editButton.setOnClickListener {
                onEditClick(currency)
            }
            itemView.setOnClickListener {
                onItemClick(currency)
            }
        }
    }
}



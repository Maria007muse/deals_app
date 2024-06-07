package ru.rsue.deals_app.deals

data class Deal(
    val id: Int,
    val type_id: Int,
    val place_id: Int,
    val currency_id: Int,
    val ticker: String,
    val order_number: String,
    val deal_number: String,
    val deal_quantity: Int,
    val deal_price: Double,
    val deal_total_cost: Double,
    val deal_trader: String,
    val deal_commission: Double
)
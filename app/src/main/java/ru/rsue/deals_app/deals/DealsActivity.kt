package ru.rsue.deals_app.deals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import ru.rsue.deals_app.R
import ru.rsue.deals_app.api.*
import ru.rsue.deals_app.currencies.Currency
import ru.rsue.deals_app.databinding.ActivityDealsBinding
import ru.rsue.deals_app.places.DealPlace
import ru.rsue.deals_app.types.DealType

class DealsActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityDealsBinding
    private lateinit var dealsRecyclerView: RecyclerView
    private lateinit var adapter: DealsAdapter
    private val api = ApiFactory.dealApi
    private val deals = mutableListOf<Deal>()
    private val types = mutableListOf<DealType>()
    private val places = mutableListOf<DealPlace>()
    private val currencies = mutableListOf<Currency>()

    // Инициализация Job для работы с корутинами
    private val job = Job()

    // Определение контекста корутины
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDealsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dealsRecyclerView = binding.rvDeals
        dealsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DealsAdapter(deals, ::deleteDeal, ::showEditDealDialog, ::showDealDetails)
        dealsRecyclerView.adapter = adapter

        loadDeals()
        loadTypes()
        loadPlaces()
        loadCurrencies()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deals, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_deal -> {
                showAddDealDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDealDetails(deal: Deal) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getDeal(deal.id).await() }
            if (response.isSuccessful) {
                val dealDetails = response.body()
                dealDetails?.let { showDealDetailsDialog(it) }
            } else {
                Toast.makeText(this@DealsActivity, "Ошибка загрузки данных о сделке", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка загрузки данных о сделке: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDealDetailsDialog(deal: Deal) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_deal_details, null)

        val tvDealType = dialogView.findViewById<TextView>(R.id.tvDealType)
        val tvDealPlace = dialogView.findViewById<TextView>(R.id.tvDealPlace)
        val tvDealCurrency = dialogView.findViewById<TextView>(R.id.tvDealCurrency)
        val tvDealTicker = dialogView.findViewById<TextView>(R.id.tvDealTicker)
        val tvOrderNumber = dialogView.findViewById<TextView>(R.id.tvOrderNumber)
        val tvDealNumber = dialogView.findViewById<TextView>(R.id.tvDealNumber)
        val tvDealQuantity = dialogView.findViewById<TextView>(R.id.tvDealQuantity)
        val tvDealPrice = dialogView.findViewById<TextView>(R.id.tvDealPrice)
        val tvDealTotalCost = dialogView.findViewById<TextView>(R.id.tvDealTotalCost)
        val tvDealTrader = dialogView.findViewById<TextView>(R.id.tvDealTrader)
        val tvDealCommission = dialogView.findViewById<TextView>(R.id.tvDealCommission)

        tvDealType.text = types.find { it.id == deal.type_id }?.type
        tvDealPlace.text = places.find { it.id == deal.place_id }?.deal_place_short
        tvDealCurrency.text = currencies.find { it.id == deal.currency_id }?.currency_short
        tvDealTicker.text = deal.ticker
        tvOrderNumber.text = deal.order_number
        tvDealNumber.text = deal.deal_number
        tvDealQuantity.text = deal.deal_quantity.toString()
        tvDealPrice.text = deal.deal_price.toString()
        tvDealTotalCost.text = deal.deal_total_cost.toString()
        tvDealTrader.text = deal.deal_trader
        tvDealCommission.text = deal.deal_commission.toString()

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnOk).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadDeals() = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getDeals().await() }
            if (response.isSuccessful) {
                val newDeals = response.body() ?: emptyList()
                deals.clear()
                deals.addAll(newDeals)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@DealsActivity, "Ошибка загрузки сделок", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка загрузки сделок: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTypes() = launch {
        try {
            val response = ApiFactory.dealTypeApi.getDealTypes().await()
            if (response.isSuccessful) {
                val newTypes = response.body() ?: emptyList()
                types.clear()
                types.addAll(newTypes)
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка загрузки типов: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPlaces() = launch {
        try {
            val response = ApiFactory.dealPlaceApi.getDealPlaces().await()
            if (response.isSuccessful) {
                val newPlaces = response.body() ?: emptyList()
                places.clear()
                places.addAll(newPlaces)
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка загрузки мест: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCurrencies() = launch {
        try {
            val response = ApiFactory.dealCurrencyApi.getCurrencies().await()
            if (response.isSuccessful) {
                val newCurrencies = response.body() ?: emptyList()
                currencies.clear()
                currencies.addAll(newCurrencies)
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка загрузки валют: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteDeal(deal: Deal) = launch {
        try {
            val response = api.deleteDeal(deal.id).await()
            if (response.isSuccessful) {
                deals.remove(deal)
                adapter.notifyDataSetChanged()
                Toast.makeText(this@DealsActivity, "Сделка удалена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@DealsActivity, "Ошибка удаления сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка удаления сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddDealDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deal, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        setupDealDialog(dialogView, dialog)
        dialog.show()
    }

    private fun showEditDealDialog(deal: Deal) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deal, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        setupDealDialog(dialogView, dialog, deal)
        dialog.show()
    }

    private fun setupDealDialog(view: View, dialog: AlertDialog, deal: Deal? = null) {
        val etTicker = view.findViewById<EditText>(R.id.etTicker)
        val etOrderNumber = view.findViewById<EditText>(R.id.etOrderNumber)
        val etDealNumber = view.findViewById<EditText>(R.id.etDealNumber)
        val etDealQuantity = view.findViewById<EditText>(R.id.etDealQuantity)
        val etDealPrice = view.findViewById<EditText>(R.id.etDealPrice)
        val etDealTotalCost = view.findViewById<EditText>(R.id.etDealTotalCost)
        val etDealTrader = view.findViewById<EditText>(R.id.etDealTrader)
        val etDealCommission = view.findViewById<EditText>(R.id.etDealCommission)
        val spDealType = view.findViewById<Spinner>(R.id.spDealType)
        val spDealPlace = view.findViewById<Spinner>(R.id.spDealPlace)
        val spDealCurrency = view.findViewById<Spinner>(R.id.spDealCurrency)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types.map { it.type })
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDealType.adapter = typeAdapter

        val placeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places.map { it.deal_place_short })
        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDealPlace.adapter = placeAdapter

        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies.map { it.currency_short })
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDealCurrency.adapter = currencyAdapter

        if (deal != null) {
            etTicker.setText(deal.ticker)
            etOrderNumber.setText(deal.order_number)
            etDealNumber.setText(deal.deal_number)
            etDealQuantity.setText(deal.deal_quantity.toString())
            etDealPrice.setText(deal.deal_price.toString())
            etDealTotalCost.setText(deal.deal_total_cost.toString())
            etDealTrader.setText(deal.deal_trader)
            etDealCommission.setText(deal.deal_commission.toString())
            spDealType.setSelection(types.indexOfFirst { it.id == deal.type_id })
            spDealPlace.setSelection(places.indexOfFirst { it.id == deal.place_id })
            spDealCurrency.setSelection(currencies.indexOfFirst { it.id == deal.currency_id })
        }

        btnSubmit.setOnClickListener {
            val ticker = etTicker.text.toString()
            val orderNumber = etOrderNumber.text.toString()
            val dealNumber = etDealNumber.text.toString()
            val dealQuantity = etDealQuantity.text.toString().toIntOrNull()
            val dealPrice = etDealPrice.text.toString().toDoubleOrNull()
            val dealTotalCost = etDealTotalCost.text.toString().toDoubleOrNull()
            val dealTrader = etDealTrader.text.toString()
            val dealCommission = etDealCommission.text.toString().toDoubleOrNull()
            val typeId = types[spDealType.selectedItemPosition].id
            val placeId = places[spDealPlace.selectedItemPosition].id
            val currencyId = currencies[spDealCurrency.selectedItemPosition].id

            if (ticker.isNotBlank() && orderNumber.isNotBlank() && dealNumber.isNotBlank() && dealQuantity != null &&
                dealPrice != null && dealTotalCost != null && dealTrader.isNotBlank() && dealCommission != null
            ) {
                if (deal == null) {
                    createDeal(Deal(0, typeId, placeId, currencyId, ticker, orderNumber, dealNumber, dealQuantity, dealPrice, dealTotalCost, dealTrader, dealCommission), dialog)
                } else {
                    updateDeal(deal.copy(type_id = typeId, place_id = placeId, currency_id = currencyId, ticker = ticker, order_number = orderNumber, deal_number = dealNumber, deal_quantity = dealQuantity, deal_price = dealPrice, deal_total_cost = dealTotalCost, deal_trader = dealTrader, deal_commission = dealCommission), dialog)
                }
            } else {
                Toast.makeText(this, "Все поля должны быть заполнены и корректно введены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createDeal(deal: Deal, dialog: AlertDialog) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.createDeal(deal).await() }
            if (response.isSuccessful) {
                loadDeals()
                dialog.dismiss()
            } else {
                Toast.makeText(this@DealsActivity, "Ошибка добавления сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка добавления сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDeal(deal: Deal, dialog: AlertDialog) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.updateDeal(deal.id, deal).await() }
            if (response.isSuccessful) {
                loadDeals()
                dialog.dismiss()
            } else {
                Toast.makeText(this@DealsActivity, "Ошибка обновления сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealsActivity, "Ошибка обновления сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }
}

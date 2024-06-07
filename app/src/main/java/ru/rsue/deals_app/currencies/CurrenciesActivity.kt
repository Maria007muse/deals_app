package ru.rsue.deals_app.currencies

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import ru.rsue.deals_app.api.ApiFactory
import retrofit2.Response
import androidx.recyclerview.widget.RecyclerView
import ru.rsue.deals_app.R
import ru.rsue.deals_app.databinding.ActivityCurrenciesBinding
import kotlin.coroutines.CoroutineContext

class CurrenciesActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityCurrenciesBinding
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var adapter: CurrenciesAdapter
    private val api = ApiFactory.dealCurrencyApi
    private val currencies = mutableListOf<Currency>()
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrenciesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currenciesRecyclerView = binding.rvCurrencies
        currenciesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CurrenciesAdapter(currencies, ::deleteCurrency, ::showEditCurrencyDialog, ::showCurrencyDetails)
        currenciesRecyclerView.adapter = adapter

        loadCurrencies()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_currencies, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_currency -> {
                showAddCurrencyDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCurrencyDetails(currency: Currency) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getCurrency(currency.id).await() }
            if (response.isSuccessful) {
                val currencyDetails = response.body()
                currencyDetails?.let { showCurrencyDetailsDialog(it) }
            } else {
                Toast.makeText(this@CurrenciesActivity, "Ошибка загрузки данных валюты", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@CurrenciesActivity, "Ошибка загрузки данных валюты: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCurrencyDetailsDialog(currency: Currency) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_currency_details, null)
        val tvCurrencyFull = dialogView.findViewById<TextView>(R.id.tvCurrencyFull)
        val tvCurrencyShort = dialogView.findViewById<TextView>(R.id.tvCurrencyShort)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        tvCurrencyFull.text = currency.currency_full
        tvCurrencyShort.text = currency.currency_short

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteCurrency(currency: Currency) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.deleteCurrency(currency.id).await() }
            if (response.isSuccessful) {
                currencies.remove(currency)
                adapter.notifyDataSetChanged()
                Toast.makeText(this@CurrenciesActivity, "Валюта удалена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@CurrenciesActivity, "Ошибка удаления валюты", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@CurrenciesActivity, "Ошибка удаления валюты: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddCurrencyDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_currency, null)
        val etCurrencyFull = dialogView.findViewById<EditText>(R.id.etCurrencyFull)
        val etCurrencyShort = dialogView.findViewById<EditText>(R.id.etCurrencyShort)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSubmit.setOnClickListener {
            val currencyFull = etCurrencyFull.text.toString()
            val currencyShort = etCurrencyShort.text.toString()
            if (currencyFull.isNotBlank() && currencyShort.isNotBlank()) {
                createCurrency(currencyFull, currencyShort)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите полное и короткое название валюты", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun createCurrency(currencyFull: String, currencyShort: String) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.createCurrency(Currency(0, currencyFull, currencyShort)).await() }
            if (response.isSuccessful) {
                loadCurrencies()
            } else {
                Toast.makeText(this@CurrenciesActivity, "Ошибка добавления валюты", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@CurrenciesActivity, "Ошибка добавления валюты: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCurrencies() = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getCurrencies().await() }
            if (response.isSuccessful) {
                val newCurrencies = response.body() ?: emptyList()
                currencies.clear()
                currencies.addAll(newCurrencies)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@CurrenciesActivity, "Ошибка загрузки валют", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@CurrenciesActivity, "Ошибка загрузки валют: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditCurrencyDialog(currency: Currency) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_currency, null)
        val etCurrencyFull = dialogView.findViewById<EditText>(R.id.etCurrencyFull)
        val etCurrencyShort = dialogView.findViewById<EditText>(R.id.etCurrencyShort)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        etCurrencyFull.setText(currency.currency_full)
        etCurrencyShort.setText(currency.currency_short)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSubmit.setOnClickListener {
            val updatedCurrencyFull = etCurrencyFull.text.toString()
            val updatedCurrencyShort = etCurrencyShort.text.toString()
            if (updatedCurrencyFull.isNotBlank() && updatedCurrencyShort.isNotBlank()) {
                updateCurrency(currency.copy(currency_full = updatedCurrencyFull, currency_short = updatedCurrencyShort))
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите полное и короткое название валюты", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateCurrency(currency: Currency) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.updateCurrency(currency.id, currency).await() }
            if (response.isSuccessful) {
                loadCurrencies()
            } else {
                Toast.makeText(this@CurrenciesActivity, "Ошибка обновления валюты", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@CurrenciesActivity, "Ошибка обновления валюты: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

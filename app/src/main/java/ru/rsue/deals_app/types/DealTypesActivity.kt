package ru.rsue.deals_app.types

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
import kotlin.coroutines.CoroutineContext
import androidx.recyclerview.widget.RecyclerView
import ru.rsue.deals_app.R
import ru.rsue.deals_app.databinding.ActivityDealTypesBinding

class DealTypesActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityDealTypesBinding
    private lateinit var dealTypesRecyclerView: RecyclerView
    private lateinit var adapter: DealTypesAdapter
    private val api = ApiFactory.dealTypeApi
    private val dealTypes = mutableListOf<DealType>()
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        binding = ActivityDealTypesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dealTypesRecyclerView = binding.rvDealTypes
        dealTypesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DealTypesAdapter(dealTypes, ::deleteDealType, ::showEditDealTypeDialog, ::showDealTypeDetails)
        dealTypesRecyclerView.adapter = adapter

        loadDealTypes()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deal_types, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_deal_type -> {
                showAddDealTypeDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDealTypeDetails(dealType: DealType) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getDealType(dealType.id).await() }
            if (response.isSuccessful) {
                val dealTypeDetails = response.body()
                dealTypeDetails?.let { showDealTypeDetailsDialog(it) }
            } else {
                Toast.makeText(this@DealTypesActivity, "Ошибка загрузки данных о типе сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealTypesActivity, "Ошибка загрузки данных о типе сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDealTypeDetailsDialog(dealType: DealType) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_deal_type_details, null)
        val tvDealType = dialogView.findViewById<TextView>(R.id.tvDealType)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        tvDealType.text = dealType.type

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteDealType(dealType: DealType) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.deleteDealType(dealType.id).await() }
            if (response.isSuccessful) {
                dealTypes.remove(dealType)
                adapter.notifyDataSetChanged()
                Toast.makeText(this@DealTypesActivity, "Тип сделки удален", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@DealTypesActivity, "Ошибка удаления типа сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealTypesActivity, "Ошибка удаления типа сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddDealTypeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deal_type, null)
        val etDealType = dialogView.findViewById<EditText>(R.id.etDealType)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSubmit.setOnClickListener {
            val newDealType = etDealType.text.toString()
            if (newDealType.isNotBlank() && newDealType.matches(Regex("^[a-zA-Zа-яА-Я]+$"))) {
                createDealType(newDealType)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите корректный тип сделки (только буквы)", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun createDealType(type: String) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.createDealType(DealType(0, type)).await() }
            if (response.isSuccessful) {
                loadDealTypes()
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Ошибка добавления типа сделки"
                Toast.makeText(this@DealTypesActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealTypesActivity, "Ошибка добавления типа сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDealTypes() = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getDealTypes().await() }
            if (response.isSuccessful) {
                val newDealTypes = response.body() ?: emptyList()
                dealTypes.clear()
                dealTypes.addAll(newDealTypes)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@DealTypesActivity, "Ошибка загрузки типов сделок", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealTypesActivity, "Ошибка загрузки типов сделок: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDealTypeDialog(dealType: DealType) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deal_type, null)
        val etDealType = dialogView.findViewById<EditText>(R.id.etDealType)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        etDealType.setText(dealType.type)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSubmit.setOnClickListener {
            val updatedDealType = etDealType.text.toString()
            if (updatedDealType.isNotBlank() && updatedDealType.matches(Regex("^[a-zA-Zа-яА-Я]+$"))) {
                updateDealType(dealType.copy(type = updatedDealType))
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите корректный тип сделки (только буквы)", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateDealType(dealType: DealType) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.updateDealType(dealType.id, dealType).await() }
            if (response.isSuccessful) {
                loadDealTypes()
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Ошибка обновления типа сделки"
                Toast.makeText(this@DealTypesActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealTypesActivity, "Ошибка обновления типа сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

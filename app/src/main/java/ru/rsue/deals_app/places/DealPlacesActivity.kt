package ru.rsue.deals_app.places

import android.annotation.SuppressLint
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
import ru.rsue.deals_app.databinding.ActivityDealPlacesBinding
import kotlin.coroutines.CoroutineContext

class DealPlacesActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityDealPlacesBinding
    private lateinit var dealPlacesRecyclerView: RecyclerView
    private lateinit var adapter: DealPlacesAdapter
    private val api = ApiFactory.dealPlaceApi
    private val dealPlaces = mutableListOf<DealPlace>()
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDealPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        job = Job()

        dealPlacesRecyclerView = binding.rvDealPlaces
        dealPlacesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DealPlacesAdapter(dealPlaces, ::deleteDealPlace, ::showEditDealPlaceDialog, ::showDealPlaceDetails)
        dealPlacesRecyclerView.adapter = adapter

        loadDealPlaces()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deal_places, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_deal_place -> {
                showAddDealPlaceDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDealPlaceDetails(dealPlace: DealPlace) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getDealPlace(dealPlace.id).await() }
            if (response.isSuccessful) {
                val dealPlaceDetails = response.body()
                dealPlaceDetails?.let { showDealPlaceDetailsDialog(it) }
            } else {
                Toast.makeText(this@DealPlacesActivity, "Ошибка загрузки данных места проведения сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealPlacesActivity, "Ошибка загрузки данных места проведения сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDealPlaceDetailsDialog(dealPlace: DealPlace) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_deal_place_details, null)
        val tvDealPlaceFull = dialogView.findViewById<TextView>(R.id.tvDealPlaceFull)
        val tvDealPlaceShort = dialogView.findViewById<TextView>(R.id.tvDealPlaceShort)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        tvDealPlaceFull.text = dealPlace.deal_place_full
        tvDealPlaceShort.text = dealPlace.deal_place_short

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteDealPlace(dealPlace: DealPlace) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.deleteDealPlace(dealPlace.id).await() }
            if (response.isSuccessful) {
                dealPlaces.remove(dealPlace)
                adapter.notifyDataSetChanged()
                Toast.makeText(this@DealPlacesActivity, "Место проведения сделки удалено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@DealPlacesActivity, "Ошибка удаления места проведения сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealPlacesActivity, "Ошибка удаления места проведения сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddDealPlaceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deal_place, null)
        val etDealPlaceFull = dialogView.findViewById<EditText>(R.id.etDealPlaceFull)
        val etDealPlaceShort = dialogView.findViewById<EditText>(R.id.etDealPlaceShort)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSubmit.setOnClickListener {
            val newDealPlaceFull = etDealPlaceFull.text.toString()
            val newDealPlaceShort = etDealPlaceShort.text.toString()
            if (newDealPlaceFull.isNotBlank() && newDealPlaceShort.isNotBlank()) {
                createDealPlace(newDealPlaceFull, newDealPlaceShort)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите полное и краткое название места проведения сделки", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun createDealPlace(dealPlaceFull: String, dealPlaceShort: String) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.createDealPlace(DealPlace(0, dealPlaceFull, dealPlaceShort)).await() }
            if (response.isSuccessful) {
                loadDealPlaces()
            } else {
                Toast.makeText(this@DealPlacesActivity, "Ошибка добавления места проведения сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealPlacesActivity, "Ошибка добавления места проведения сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDealPlaces() = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.getDealPlaces().await() }
            if (response.isSuccessful) {
                val newDealPlaces = response.body() ?: emptyList()
                dealPlaces.clear()
                dealPlaces.addAll(newDealPlaces)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@DealPlacesActivity, "Ошибка загрузки мест проведения сделок", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealPlacesActivity, "Ошибка загрузки мест проведения сделок: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDealPlaceDialog(dealPlace: DealPlace) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deal_place, null)
        val etDealPlaceFull = dialogView.findViewById<EditText>(R.id.etDealPlaceFull)
        val etDealPlaceShort = dialogView.findViewById<EditText>(R.id.etDealPlaceShort)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)
        etDealPlaceFull.setText(dealPlace.deal_place_full)
        etDealPlaceShort.setText(dealPlace.deal_place_short)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSubmit.setOnClickListener {
            val updatedDealPlaceFull = etDealPlaceFull.text.toString()
            val updatedDealPlaceShort = etDealPlaceShort.text.toString()
            if (updatedDealPlaceFull.isNotBlank() && updatedDealPlaceShort.isNotBlank()) {
                updateDealPlace(dealPlace.copy(deal_place_full = updatedDealPlaceFull, deal_place_short = updatedDealPlaceShort))
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите полное и краткое название места проведения сделки", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateDealPlace(dealPlace: DealPlace) = launch {
        try {
            val response = withContext(Dispatchers.IO) { api.updateDealPlace(dealPlace.id, dealPlace).await() }
            if (response.isSuccessful) {
                loadDealPlaces()
            } else {
                Toast.makeText(this@DealPlacesActivity, "Ошибка обновления места проведения сделки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DealPlacesActivity, "Ошибка обновления места проведения сделки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}


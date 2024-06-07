package ru.rsue.deals_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.rsue.deals_app.currencies.CurrenciesActivity
import ru.rsue.deals_app.databinding.ActivityMainBinding
import ru.rsue.deals_app.deals.DealsActivity
import ru.rsue.deals_app.places.DealPlacesActivity
import ru.rsue.deals_app.types.DealTypesActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDealTypes.setOnClickListener {
            val intent = Intent(this, DealTypesActivity::class.java)
            startActivity(intent)
        }

        binding.btnDealPlaces.setOnClickListener {
            val intent = Intent(this, DealPlacesActivity::class.java)
            startActivity(intent)
        }

        binding.btnCurrencies.setOnClickListener {
            val intent = Intent(this, CurrenciesActivity::class.java)
            startActivity(intent)
        }

        binding.btnDeals.setOnClickListener {
            val intent = Intent(this, DealsActivity::class.java)
            startActivity(intent)
        }
    }
}

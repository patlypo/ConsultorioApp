package com.example.consultorio

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consultorio.databinding.ActivityCitasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ActivityCitas : AppCompatActivity() {

    private lateinit var binding: ActivityCitasBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var citaAdapter: CitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.recyclerViewAppointments.layoutManager = LinearLayoutManager(this)
        citaAdapter = CitaAdapter(mutableListOf(), emptyMap())
        binding.recyclerViewAppointments.adapter = citaAdapter

        binding.navView.selectedItemId = R.id.segundoBottomItem
        binding.navView.setOnNavigationItemReselectedListener { item ->
            when(item.itemId){
                R.id.primerBottomItem ->{
                    val  intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.segundoBottomItem ->{
                    val intent = Intent(this,ActivityCitas::class.java)
                    startActivity(intent)
                    true
                }
                R.id.cuartoBottomitem ->{
                    val intent = Intent(this, user::class.java)
                    startActivity(intent)
                    true

                }
            }
        }


        observeCitas()
        viewModel.loadCitas()

        binding.imageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun observeCitas() {
        viewModel.citas.observe(this, Observer { citas ->
            if (citas.isNotEmpty()) {
                updateCitasList(citas)
            } else {
                Toast.makeText(this, "No tienes citas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCitasList(citasList: List<citaModel>) {
        citaAdapter.updateItems(citasList)
    }
}

package com.example.consultorio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consultorio.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCategory()
        shownombreuser()

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

    }

    private fun shownombreuser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId)

            databaseReference.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    binding.textView.text = "Hola, $name"
                } else {
                    Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No se encontró un usuario autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.category.observe(this, Observer { categories ->
            val categoryMap = categories.associateBy({ it.Id }, { it.Name })

            binding.viewCategory.layoutManager = createLinearLayoutManager()
            binding.viewCategory.adapter = CategoryAdapter(categories){selectedCategory ->
                filterDoctorsByCategory(selectedCategory,categoryMap)

            }
            binding.progressBarCategory.visibility = View.GONE

            initDoctor(categoryMap)
        })
        viewModel.loadCategory()
    }

    private fun filterDoctorsByCategory(selectedCategory: CategoryModel, categoryMap: Map<Int, String>) {
        val filterdoctor = viewModel.doctors.value?.filter { doctor->
            doctor.categoryId == selectedCategory.Id

        }?: emptyList()
        binding.recyclerViewDoctor.adapter = DoctorAdapter(filterdoctor.toMutableList(), categoryMap )
    }


    private fun initDoctor(categoryMap: Map<Int,String>) {
        binding.progressBarDoctor.visibility = View.VISIBLE

        viewModel.doctors.observe(this, Observer { doctors ->
            binding.recyclerViewDoctor.layoutManager = createLinearLayoutManager()
            binding.recyclerViewDoctor.adapter = DoctorAdapter(doctors, categoryMap)
            binding.progressBarDoctor.visibility = View.GONE
        })
            viewModel.loadDoctors()
        }

    private fun createLinearLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
    private fun createGridLayoutManager(): GridLayoutManager {
        return GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
    }

}
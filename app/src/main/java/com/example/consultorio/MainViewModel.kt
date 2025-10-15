package com.example.consultorio

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class MainViewModel ():ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _doctors = MutableLiveData<MutableList<DoctorsModel>>()
    private val _citas = MutableLiveData<MutableList<citaModel>>()

    val category: LiveData<MutableList<CategoryModel>> = _category
    val doctors: LiveData<MutableList<DoctorsModel>> = _doctors
    val citas:LiveData<MutableList<citaModel>> = _citas

    private val categoryMap = mutableMapOf<Int, String>()

    private val imageResourceMap = mapOf(
        /* 0*/   "dentistaimg" to R.drawable.odontologia,
        /* 1*/    "gastro" to R.drawable.gastro,
        /* 2*/    "cardiologiaimg" to R.drawable.cardiologia,
        /* 3*/    "endocrinologia" to R.drawable.endocrinologia
    )
    private val imageResourceMapDoctor = mapOf(
        "doctor1" to R.drawable.doctor1remove,
        "doctor3" to R.drawable.enriquedoc,
        "doctor2" to R.drawable.doctor2remove,
        "doctora2" to R.drawable.doctora2remove
    )

    fun loadCategory() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        val resourceId = imageResourceMap[list.Picture] ?: R.drawable.dentistry
                        val category = CategoryModel(
                            Id = list.Id,
                            Name = list.Name,
                            Picture = resourceId.toString()
                        )
                        lists.add(category)
                        categoryMap[category.Id] = category.Name

                    }
                }
                _category.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)

            }
        })
    }

    fun loadDoctors() {
        val ref = firebaseDatabase.getReference("Doctors")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<DoctorsModel>()

                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(DoctorsModel::class.java)

                    if (list != null) {
                        val resourceId = imageResourceMapDoctor[list.Picture] ?: R.drawable.doctor1remove
                        val categoryname = categoryMap[list.categoryId.toInt()] ?: "Sin categoría"

                        lists.add(
                            DoctorsModel(
                                Address = list.Address,
                                Biography = list.Biography,
                                Id = list.Id,
                                Name = list.Name,
                                Picture = resourceId.toString(),
                                categoryId = list.categoryId,
                                Expriense = list.Expriense,
                                Location = list.Location,
                                Mobile = list.Mobile,
                                Patiens = list.Patiens,
                                Rating = list.Rating,
                                Site = list.Site,
                                Time = list.Time
                            )
                        )
                    }
                }
                _doctors.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
    }
    fun loadCitas() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val ref = firebaseDatabase.getReference("Citas").orderByChild("userid").equalTo(currentUserId)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val citasList = mutableListOf<citaModel>()
                    for (childSnapshot in snapshot.children) {
                        val cita = childSnapshot.getValue(citaModel::class.java)
                        if (cita != null) {
                            cita.key = childSnapshot.key ?: ""
                            citasList.add(cita)
                        }
                    }
                    _citas.value = citasList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", error.message)
                }
            })
        }
    }



}
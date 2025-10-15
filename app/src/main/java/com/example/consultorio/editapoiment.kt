package com.example.consultorio

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.consultorio.databinding.ActivityDetailBinding
import com.example.consultorio.databinding.ActivityEditapoimentBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class editapoiment : AppCompatActivity() {
    private lateinit var binding: ActivityEditapoimentBinding
    private var citaId: String = ""
    private var originalDate: String = ""
    private var originalTime: String = ""
    private var originalDoctorid: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditapoimentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getStringExtra("citaid")?:""
        originalDate = intent.getStringExtra("date")?:""
        originalTime = intent.getStringExtra("time")?:""
        originalDoctorid = intent.getIntExtra("doctorId", 0)

        binding.apply {
            dateselected.text = originalDate
            timetxt.text = originalTime
        }
        binding.btnSelectDate.setOnClickListener{
            showDatePicker { selectedDate ->
                binding.dateselected.text = selectedDate
            }
        }
        fetchAvailableTimes(binding.timeselected)

        binding.btnConfirm.setOnClickListener {
            save()
        }


    }

    private fun save() {
        val newDate = binding.dateselected.text.toString()
        val newTime = binding.timeselected.selectedItem?.toString() ?: ""
        if (newDate.isNotEmpty() && newTime.isNotEmpty()) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Citas")
            val updatedCita = mapOf(
                "date" to newDate,
                "time" to newTime,
                "doctorId" to originalDoctorid
            )

            databaseReference.child(citaId).updateChildren(updatedCita).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cita actualizada correctamente.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error al actualizar la cita.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Por favor selecciona una fecha y hora.", Toast.LENGTH_SHORT).show()
        }


    }
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            onDateSelected("$d/${m + 1}/$y")
        }, year, month, day).show()
    }
    private fun fetchAvailableTimes(timeselected: Spinner, selectedTime: String? = null) {

        val times = horario.getAvailableTimes()

        if (times.isNotEmpty()) {
            populateTimeSpinner(times, timeselected, selectedTime)
        } else {
            Toast.makeText(this, "No hay horarios disponibles.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateTimeSpinner(times: List<String>, timeselected: Spinner, selectedTime:String?) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, times)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeselected.adapter = adapter


    }

}
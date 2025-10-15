package com.example.consultorio

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.consultorio.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class activity_detail : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
        binding.makeBtn.setOnClickListener {
            showMakeAppointment()
        }
    }

    private fun showMakeAppointment() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.makeapoiment)

        val btnSelectDate: LinearLayout = dialog.findViewById(R.id.btn_select_date)
        val btnConfirm: Button = dialog.findViewById(R.id.btn_confirm)
        val timeselected: Spinner = dialog.findViewById(R.id.timeselected)
        val dateselected: TextView = dialog.findViewById(R.id.dateselected)
        val time:TextView = dialog.findViewById(R.id.timetxt)

        var selectedDate = ""
        var selectedTime = ""

        btnSelectDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                dateselected.text = date
                fetchAvailableTimes(timeselected, selectedDate)
                time.text = selectedTime

            }
        }

        btnConfirm.setOnClickListener {
            val selectedTime = timeselected.selectedItem?.toString() ?: ""
            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                savedate(selectedDate,selectedTime)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, selecciona fecha y hora", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
    private fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    private fun savedate(selectedDate: String, selectedTime: String) {
        val userId = getUserId()
        val cita = citaModel(
            doctorId = item.Id,
            doctorname = item.Name,
            date = selectedDate,
            time = selectedTime,
            userid = userId
        )
        val database = FirebaseDatabase.getInstance().reference
        val ref = database.child("Citas").push()

        ref.setValue(cita).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Cita agendada", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Error al agendar cita", Toast.LENGTH_SHORT).show()

            }
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

    private fun fetchAvailableTimes(timeselected: Spinner, selectedDate: String) {
        val database = FirebaseDatabase.getInstance().reference.child("Citas")
        val times = horario.getAvailableTimes().toMutableList()

        database.orderByChild("date").equalTo(selectedDate).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (citaSnapshot in snapshot.children) {
                    val occupiedTime = citaSnapshot.child("time").value.toString()
                    times.remove(occupiedTime)
                }
            }
            if (times.isNotEmpty()) {
                populateTimeSpinner(times, timeselected)
            } else {
                Toast.makeText(this, "No hay horarios disponibles para esta fecha.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al verificar horarios: ${it.message}", Toast.LENGTH_SHORT).show()
            Log.d("erroro al agendar ita", it.message.toString())
        }

    }

    private fun populateTimeSpinner(times: List<String>, timeselected: Spinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, times)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeselected.adapter = adapter


    }

        private fun getBundle() {
            item = intent.getParcelableExtra("object")!!
            val categoryName = intent.getStringExtra("categoryName") ?: "Sin categoría"

            binding.apply {
                titletxt.text = item.Name
                especialidadtxt.text = categoryName
                patientTxt.text = item.Patiens
                biotxt.text = item.Biography
                address.text = item.Address
                experienciatxt.text = item.Expriense.toString() + " años"
                ratingtxt.text = "${item.Rating}"
                backBtn.setOnClickListener { finish() }

                websiteBtn.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setData(Uri.parse(item.Site))
                    startActivity(i)
                }

                callBtn.setOnClickListener {
                    val uri = "tel:" + item.Mobile.trim()
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
                    startActivity(intent)
                }

                ubiBtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.Location))
                    startActivity(intent)
                }

                val resourceId = item.Picture.toInt()
                imgdetail.setImageResource(resourceId)
            }
        }
    }


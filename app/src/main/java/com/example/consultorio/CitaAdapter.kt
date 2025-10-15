package com.example.consultorio

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.consultorio.databinding.ViewholdercitaBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CitaAdapter(
    private var citasList: MutableList<citaModel>,
    private val doctorInfoMap: Map<Int, DoctorsModel>
) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    class CitaViewHolder(val binding: ViewholdercitaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ViewholdercitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citasList[position]
        val doctorInfo = doctorInfoMap[cita.doctorId]
        val formattedDate = formatDate(cita.date)

        holder.binding.apply {
            txtAppointmentDate.text = formattedDate
            txtDoctorName.text = cita.doctorname
            txtSpeciality.text =  cita.time

            imgOptions.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it)
                val inflater: MenuInflater = popupMenu.menuInflater
                inflater.inflate(R.menu.menu_options, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId){
                        R.id.editCita -> {
                            editCita(cita,cita.key, it.context)
                         true
                        }
                        R.id.deleteCita -> {
                            deleteCita(cita,cita.key, it.context)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }
    private fun editCita(cita: citaModel,key:String, context: Context){
        val intent = Intent(context, editapoiment::class.java)
        intent.putExtra("citaid", key)
        intent.putExtra("date", cita.date)
        intent.putExtra("time", cita.time)
        intent.putExtra("doctorname", cita.doctorId)

        context.startActivity(intent)


    }
    private fun deleteCita(cita: citaModel, key:String,context: Context) {
        val databaseref = FirebaseDatabase.getInstance().getReference("Citas")

        databaseref.child(key).removeValue().addOnCompleteListener {task ->
            if (task.isSuccessful){
                citasList.remove(cita)
                notifyDataSetChanged()
                Toast.makeText(context, "Cita eliminada", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context, "Error al eliminar la cita: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
        }

    override fun getItemCount(): Int = citasList.size

    fun updateItems(newItems: List<citaModel>) {
        citasList.clear()
        citasList.addAll(newItems)
        notifyDataSetChanged()
    }
    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato original
        val outputFormat = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es", "ES")) // Formato deseado

        try {
            val date = inputFormat.parse(dateString)

            return if (date != null) {
                outputFormat.format(date)
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}

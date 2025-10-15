package com.example.consultorio

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consultorio.databinding.ViewholderDoctorBinding

class DoctorAdapter(val items:MutableList<DoctorsModel>, private val categoryMap:Map<Int,String>): RecyclerView.Adapter<DoctorAdapter.Viewholder>() {
    private var context:Context?=null

    class Viewholder(val binding: ViewholderDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderDoctorBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: DoctorAdapter.Viewholder, position: Int) {
        val doctor = items[position]

        holder.binding.nameTxt.text=doctor.Name
        holder.binding.specialTxt.text = categoryMap[doctor.categoryId] ?: "Sin categoría"
        holder.binding.scoreTxt.text = doctor.Rating.toString()
        holder.binding.yearTxt.text = doctor.Expriense.toString()+" años"

        val resourceId = doctor.Picture.toInt()
        holder.binding.imgdoc.setImageResource(resourceId)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, activity_detail::class.java)
            intent.putExtra("object",items[position])
            intent.putExtra("categoryName", categoryMap[doctor.categoryId.toInt()] ?: "Sin categoría")
            context?.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = items.size

}
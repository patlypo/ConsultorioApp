package com.example.consultorio

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consultorio.databinding.ViewholderCategoryBinding

class CategoryAdapter(val items:MutableList<CategoryModel>, private val onCategoryClick: (CategoryModel) -> Unit):
    RecyclerView.Adapter<CategoryAdapter.Viewholder>() {

    private lateinit var context: Context
    inner class Viewholder (val binding: ViewholderCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                onCategoryClick(items[adapterPosition])
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.Viewholder {
        context = parent.context
        val binding= ViewholderCategoryBinding.inflate(LayoutInflater.from(context),parent, false)
        return Viewholder(binding)

    }

    override fun onBindViewHolder(holder: CategoryAdapter.Viewholder, position: Int) {
        val item = items[position]
        holder.binding.titleTxt.text = item.Name
        //holder.binding.img.setImageResource(item.Picture)
        holder.binding.img.setImageResource(item.Picture.toInt())

    }

    override fun getItemCount(): Int =items.size
}
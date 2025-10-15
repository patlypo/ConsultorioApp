package com.example.consultorio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.consultorio.databinding.ActivityMainBinding
import com.example.consultorio.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class user : AppCompatActivity() {
    private lateinit var  binding : ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnlogout.setOnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }

        binding.navView.selectedItemId = R.id.cuartoBottomitem
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

        showinfo()
    }
    private fun showinfo() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId)

            databaseReference.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val correo = snapshot.child("email").value.toString()
                    binding.userNameTextView.text = name
                    binding.userEmailTextView.text = correo
                } else {
                    Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No se encontró un usuario autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
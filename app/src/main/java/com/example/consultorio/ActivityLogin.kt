package com.example.consultorio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.consultorio.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ActivityLogin : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.textcrearcuenta1.setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
        }
        binding.bottomIngresar.setOnClickListener {
            val email = binding.textInputemail.text.toString().trim()
            val pass = binding.textInputpass.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()){
                checkUser(email,pass);
            }else{
                Toast.makeText(this,"Complete todos los campos", Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun checkUser(email:String,pass:String) {
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(this,"Nombre o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
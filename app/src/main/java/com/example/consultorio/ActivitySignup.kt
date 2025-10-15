package com.example.consultorio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.consultorio.databinding.ActivityLoginBinding
import com.example.consultorio.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ActivitySignup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var bdRef : DatabaseReference

    private lateinit var nombre: TextInputEditText
    private lateinit var textemail: TextInputEditText
    private lateinit var textpass: TextInputEditText
    private lateinit var textconfirmpass: TextInputEditText
    private lateinit var btncrearcuenta: Button

    private lateinit var login: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nombre = findViewById(R.id.textuser)
        textemail = findViewById(R.id.textInputemail)
        textpass = findViewById(R.id.textInputpassword)
        textconfirmpass = findViewById(R.id.textInputconfirmpassword)
        btncrearcuenta = findViewById(R.id.bottomcrearcuenta)
        login = findViewById(R.id.textViewcuenta)

        binding.textViewcuenta.setOnClickListener {
            onBackPressed()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.bottomcrearcuenta.setOnClickListener {
            val name = nombre.text.toString().trim()
            val email = textemail.text.toString().trim()
            val pass = textpass.text.toString().trim()
            val confirmpass = textconfirmpass.text.toString().trim()

            if (email.isNotEmpty() && name.isNotEmpty() && pass.isNotEmpty() && confirmpass.isNotEmpty()){
                if(pass == confirmpass){
                    login(name,email,pass)

                }else{
                    Toast.makeText(this, "La contraseña no es la misma", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Complete todos los campos", Toast.LENGTH_LONG).show()

            }

        }


    }
    private fun login(name:String, email:String, pass:String){
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
            if (it.isSuccessful){
                addUserToDatabase(name,email,firebaseAuth.currentUser?.uid!!)
                val intent = Intent(this,MainActivity::class.java)
                finish()
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                Log.e("FirebaseError", it.exception.toString())


            }
        }

    }

    private fun addUserToDatabase(name:String, email: String, uid:String){
        bdRef = FirebaseDatabase.getInstance().getReference()

        bdRef.child("User").child(uid).setValue(UserModel(name,email,uid))

    }
}
package com.example.digigit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
class LoginScreenActivity: AppCompatActivity() {
    internal lateinit var logIn:Button
    internal lateinit var register:Button
    internal lateinit var etEmail:EditText
    internal lateinit var etPassword:EditText
    internal lateinit var myEmail:String
    internal lateinit var myPassword:String
    internal lateinit var myAuth:FirebaseAuth

     override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        myAuth = FirebaseAuth.getInstance()
        if (myAuth.getCurrentUser() != null)
        {
            val homeIntent = Intent(this@LoginScreenActivity, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        }
        logIn = findViewById(R.id.myButton)
        etEmail = findViewById(R.id.myEmail)
        etPassword = findViewById(R.id.myPassword)
        register = findViewById(R.id.regButton)
        logIn.setOnClickListener(object: View.OnClickListener {
           override fun onClick(v:View) {
                myEmail = etEmail.getText().toString().trim()
                myPassword = etPassword.getText().toString().trim()
                CreateUser()
            }
        })
        register.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val regIntent = Intent(this@LoginScreenActivity, RegisterActivity::class.java)
                startActivity(regIntent)
            }
        })
    }
    private fun CreateUser() {
        myAuth.signInWithEmailAndPassword(myEmail, myPassword).addOnCompleteListener {

                if (it.isSuccessful())
                {
                    val homeIntent = Intent(this@LoginScreenActivity, MainActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                }
                else
                {
                    Toast.makeText(this@LoginScreenActivity, "Error is " + it.getException(), Toast.LENGTH_SHORT).show()
                }
            }

    }
}
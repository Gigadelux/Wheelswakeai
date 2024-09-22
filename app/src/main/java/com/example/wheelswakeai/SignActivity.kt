package com.example.wheelswakeai
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignActivity:AppCompatActivity(){
    private lateinit var editTextPassword: EditText
    private lateinit var editEmail: EditText
    private var isReg: Boolean = false
    private lateinit var signIntro: TextView
    private lateinit var retypePassword:EditText
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registerlogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        signIntro = findViewById(R.id.loginIntro)
        val changeButton = findViewById<Button>(R.id.Register)
        val actionButton = findViewById<Button>(R.id.button)
        val resetPassw = findViewById<Button>(R.id.resetPssw)
        editEmail = findViewById(R.id.email)
        retypePassword = findViewById(R.id.retypePssw)
        resetPassw.visibility = View.GONE
        firebaseAuth = FirebaseAuth.getInstance()
        changeButton.setOnClickListener {
            if(!this.isReg) {
                // Change the text of the button after click
                changeButton.setText("Login")
                actionButton.setText("Register")
                signIntro.setText("Already a user?")
                this.isReg = true
                retypePassword.visibility = View.VISIBLE;
                resetPassw.visibility = View.GONE
            }
            else {
                changeButton.setText("Register")
                actionButton.setText("Login")
                signIntro.setText("New to the platform?")
                this.isReg = false
                retypePassword.visibility = View.GONE
                resetPassw.visibility = View.VISIBLE
            }
        }
        actionButton.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val pass = editTextPassword.text.toString()
            var isStatusValid:Boolean = ((this.isReg && pass.equals(retypePassword.text.toString())) || (!this.isReg))&& !pass.contains(" ")
            if (email.isNotEmpty() && pass.isNotEmpty() && isStatusValid) {
                if(!isReg) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    var firestore = FirebaseFirestore.getInstance()
                    firestore.collection("users").document(email).set(hashMapOf(Pair("account",email),Pair("trips",0)))
                        .addOnSuccessListener {
                            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnSuccessListener {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error completing registration", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error saving your account", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            else if(!isStatusValid && !this.isReg){
                Toast.makeText(this, "The retype password does not correspond", Toast.LENGTH_SHORT).show()
            }
            else if(pass.contains(" ")){
                Toast.makeText(this, "Password cannot contains space!", Toast.LENGTH_SHORT).show()

            }
            else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
        resetPassw.setOnClickListener{
            val email = editEmail.text.toString()
            if(email.isEmpty()){
                Toast.makeText(this, "Email field is empty😢", Toast.LENGTH_SHORT).show()
            }
            else {
                try {
                    firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                        Toast.makeText(this, "Email sent😉", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
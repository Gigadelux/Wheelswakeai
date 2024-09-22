package com.example.wheelswakeai
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class User:Fragment() {
    private lateinit var logout_button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this.activity, SignActivity::class.java)
        // Clear the back stack so the user cannot navigate back to MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        this.activity?.finish() // Terminate the current activity
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.user, container, false)
        this.logout_button = v.findViewById<Button>(R.id.logout_button)
        var userEmail = v.findViewById<TextView>(R.id.userEmail)
        userEmail.setText(FirebaseAuth.getInstance().currentUser?.email ?: "Loading...")
        logout_button.setOnClickListener {
            logout()
        }
        return v
    }
}
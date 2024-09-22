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

class Home : Fragment() {
    private lateinit var TripButton: Button
    private lateinit var AiCameraButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.home, container, false)
        var loginIntro = v.findViewById<TextView>(R.id.welcome_text)
        this.TripButton = v.findViewById<Button>(R.id.user_trips)
        this.AiCameraButton = v.findViewById<Button>(R.id.ai_start)
        AiCameraButton.setOnClickListener {
            val intent = Intent(this.activity, AiCameraActivity::class.java)
            startActivity(intent)
        }
        TripButton.setOnClickListener{
            val intent = Intent(this.activity, TripsActivity::class.java)
            startActivity(intent)

        }
        loginIntro.setText("Welcome back "+createUsername(FirebaseAuth.getInstance().currentUser?.email.toString())+"!")
        // Inflate the layout for this fragment
        return v
    }
    fun createUsername(t:String): String {
        var emailSplitted:List<String> = t.split("@")
        var subUsername: String = emailSplitted[0].split(".")[0]
        if(subUsername.length <= 5)
            return subUsername
        else
            return subUsername.subSequence(0,5).toString()
    }

}
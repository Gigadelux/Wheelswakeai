package com.example.wheelswakeai
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wheelswakeai.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // Set the initial item as selected
        bottomNavigationView.menu.getItem(1).setChecked(true)
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(
                Color.WHITE,  // Selected color
                Color.GRAY, // Normal color
                Color.BLACK // Disabled color
            )
        )
        bottomNavigationView.itemTextColor = colorStateList
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.community -> replaceFragment(Community())
                R.id.home -> replaceFragment(Home())
                R.id.user -> replaceFragment(User())
                else -> false

            }

            true

        }
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layoutF,fragment)
        fragmentTransaction.commit()


    }


}

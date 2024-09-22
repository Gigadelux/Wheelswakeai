package com.example.wheelswakeai.review
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
import kotlin.random.Random

public class reviewsDemoList {
    fun getReviews():List<Review>{
        val review0 = Review(4.0, "Fantastic app! It saved my life many times!")
        val review1 = Review(4.0, "Helped me in my trip from NYC to LA!!!")
        val review2 = Review(4.0, "Amazing app, I need it more than ever with my new job.")
        val review3 = Review(5.0, "I have a bus company, I immediately told to every employee in my company to install this app!")
        return listOf<Review>(review0, review1, review2, review3)
    }
    fun getRandom():Review{
        return getReviews()[Random.nextInt(0,3)]
    }
}
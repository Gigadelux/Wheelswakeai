package com.example.wheelswakeai.review
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
class Review(N: Double, val description: String) {

    private var starRatio: String = ""

    init {
        starRatio = buildStarRatio(N)
    }


    private fun buildStarRatio(N: Double): String {
        val starEmoji = "⭐"
        val starCount = N.toInt()
        return starEmoji.repeat(starCount) + " $N"
    }


    fun getDescriptionText(): String {
        return description
    }

    fun getStarRatio(): String {
        return starRatio
    }
}
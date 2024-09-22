package com.example.wheelswakeai.trips

import java.time.LocalDateTime
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
data class trip (
    val date:String="",
    val description:String="",
    val time:String="",
    val speed:String="",
    val initialDate:LocalDateTime
)
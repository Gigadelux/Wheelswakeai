package com.example.wheelswakeai.review
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class FakeUser {
    fun get():Map<String,String>{
        val randomUserUrl = "https://randomuser.me/api/"
        var userName = "defaultUser"
        var userProfileImg = "https://randomuser.me/api/portraits/thumb/men/57.jpg"
        try {
            val url = URL(randomUserUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)
                val results = jsonObject.getJSONArray("results").getJSONObject(0)
                userName = results.getJSONObject("login").getString("username")
                userProfileImg = results.getJSONObject("picture").getString("large")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mapOf("username" to userName, "profileImg" to userProfileImg)
    }
}
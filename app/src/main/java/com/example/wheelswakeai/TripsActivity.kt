package com.example.wheelswakeai
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wheelswakeai.trips.MyTripsAdapter
import com.example.wheelswakeai.trips.trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TripsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var tripsList = mutableListOf<trip>()
    private lateinit var myTripsAdapter: MyTripsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trips)
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        myTripsAdapter = MyTripsAdapter(tripsList)
        recyclerView.adapter = myTripsAdapter
        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.email ?: "")
            .collection("trips")
            .get()
            .addOnSuccessListener { result ->
                tripsList.clear()
                for (document in result) {
                    val item = tripFromDoc(document)
                    tripsList.add(item)
                }
                tripsList.sortByDescending { it.initialDate }
                myTripsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }
    private fun tripFromDoc(doc:QueryDocumentSnapshot) : trip{
        var dateIntro = "DATE: "
        var description = "⚠️During the trip: "
        var timeIntro = "⌛TIME SPENT: "
        var speedIntro = "⏲️Speed: "
        dateIntro += doc.data["startDate"]
        var hasSleeped: Boolean = doc.data["sleeping"] as Boolean
        var hasYawn: Boolean = doc.data["yawning"] as Boolean
        var speed = doc.data["speed"]
        var duration: String = calculateTimeDifference(doc.data["startDate"] as String, doc.data["endDate"] as String)
        timeIntro += duration
        speedIntro += speed
        if(hasSleeped && hasYawn){
            description += "💤🥱 (pay attention you was sleepy!)"
        }else if(hasSleeped){
            description += "💤💤 (PAY ATTENTION SLEEP DANGER!!!)"
        }else if(hasYawn){
            description += "🥱🥱 (be careful! You was tired...)"
        }else{
            description += "👀👀 (good job! Fully awake🎖️)"
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val initialDateTime = LocalDateTime.parse(doc.data["startDate"] as String, formatter)
        var res : trip = trip(date = dateIntro, description = description, time = timeIntro, speed = speedIntro, initialDate = initialDateTime)
        return res
    }

    private fun calculateTimeDifference(dateTimeString1: String, dateTimeString2: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val dateTime1 = LocalDateTime.parse(dateTimeString1, formatter)
        val dateTime2 = LocalDateTime.parse(dateTimeString2, formatter)

        val duration = Duration.between(dateTime1, dateTime2)

        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        return "$days d, $hours h, $minutes min, $seconds sec"
    }
}

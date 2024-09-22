package com.example.wheelswakeai
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.wheelswakeai.review.FakeUser
import com.example.wheelswakeai.review.reviewsDemoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Community:Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.community, container, false)
        var usernameTextView = v.findViewById<TextView>(R.id.review_username)
        var descriptionTextView = v.findViewById<TextView>(R.id.review_description)
        var starRatioTextView = v.findViewById<TextView>(R.id.review_rating)
        var profileImageView = v.findViewById<ImageView>(R.id.review_profile_img)

        var randomReview = reviewsDemoList().getRandom()
        GlobalScope.launch (Dispatchers.IO){
            var user:Map<String,String> = FakeUser().get()
            launch (Dispatchers.Main){
                usernameTextView.text = user["username"]
                descriptionTextView.text = randomReview.description
                starRatioTextView.text = randomReview.getStarRatio()
                Glide.with(profileImageView.context)
                    .load(user["profileImg"])
                    .into(profileImageView)
            }
        }
        // Inflate the layout for this fragment
        return v
    }
}
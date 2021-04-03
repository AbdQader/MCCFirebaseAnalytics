package com.example.mccfirebaseanalytics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var db: FirebaseFirestore

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private var spentTime: Long = 0
    private var saveTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // initialize the database and the firebaseAnalytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        db = FirebaseFirestore.getInstance()

        // to track the product details screen
        trackScreen()

        val category = intent.getStringExtra("category")
        val productId = intent.getStringExtra("product")

        // to get the pressed product details from firebase database
        db.collection(category!!).document(productId!!)
            .get()
            .addOnSuccessListener { querySnapshot ->
                txtName.text = querySnapshot.data!!["name"].toString()
                txtSpecifications.text = querySnapshot.data!!["specifications"].toString().replace("\\n", "\n")
                Glide.with(this).load(querySnapshot.data!!["image"].toString())
                    .into(imgProduct)
            }
            .addOnFailureListener { exception ->
                Log.e("abd", exception.message!!)
            }

    }

    // start calculating the time that the user spent on this page
    override fun onResume() {
        super.onResume()
        timeSpent()
    }

    // stop calculating the time that the user spent on this page
    override fun onPause() {
        super.onPause()
        saveTime = spentTime
        handler.removeCallbacks(runnable)
    }

    // this function to track the DetailsActivity by google analytics
    private fun trackScreen() {
        val parameters = Bundle().apply {
            this.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Product Details Page")
            this.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "DetailsActivity")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, parameters)
    }

    // this function calculates the time that the user spent in the screen
    private fun timeSpent() {
        val startTime = System.currentTimeMillis()
        handler = Handler()
        runnable = Runnable {
            kotlin.run {
                val diff = System.currentTimeMillis() - startTime
                spentTime = (diff / 1000) + saveTime
                saveTimeToDB(spentTime)
                handler.postDelayed(runnable, 1000)
            }
        }
        handler.post(runnable)
    }

    // this function to save the time that the user spent on the screen in the database
    private fun saveTimeToDB(time: Long) {

        val data = mapOf("time" to time, "userId" to 1, "pageName" to "Details Page")

        db.collection("time").document("DetailsPage")
                .update(data)
                .addOnSuccessListener {
                    Log.e("abd", "Task Complete successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("abd", exception.message!!)
                }
    }

}
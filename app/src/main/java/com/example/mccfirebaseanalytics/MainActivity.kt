package com.example.mccfirebaseanalytics

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var db: FirebaseFirestore

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private var spentTime: Long = 0
    private var saveTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the database and the firebaseAnalytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        db = FirebaseFirestore.getInstance()

        // to track the category screen "home page"
        trackScreen()

        // go to food category
        btnFood.setOnClickListener {
            switchCategory("food")
        }

        // go to laptops category
        btnLaptops.setOnClickListener {
            switchCategory("laptops")
        }

        // go to phones category
        btnPhones.setOnClickListener {
            switchCategory("phones")
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

    // this function to switch between categories
    private fun switchCategory(category: String) {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }

    // this function to track the MainActivity (CategoryScreen) by google analytics
    private fun trackScreen() {
        val parameters = Bundle().apply {
            this.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Category Home Page")
            this.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
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

        val data = mapOf("time" to time, "userId" to 1, "pageName" to "Categories Page")

        db.collection("time").document("CategoriesPage")
            .update(data)
            .addOnSuccessListener {
                Log.e("abd", "Task Complete successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("abd", exception.message!!)
            }
    }

}
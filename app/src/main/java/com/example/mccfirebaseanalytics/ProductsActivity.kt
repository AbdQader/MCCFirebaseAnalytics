package com.example.mccfirebaseanalytics

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var db: FirebaseFirestore

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private var spentTime: Long = 0
    private var saveTime: Long = 0

    private lateinit var product1Id: String
    private lateinit var product2Id: String
    private lateinit var product3Id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        // initialize the database and the firebaseAnalytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        db = FirebaseFirestore.getInstance()

        // to track the category products screen
        trackScreen()

        when(intent.getStringExtra("category")) {
            "food" -> getFoodCategory()
            "laptops" -> getLaptopsCategory()
            "phones" -> getPhonesCategory()
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

    // for food category
    private fun getFoodCategory() {
        db.collection("food")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // to store the products id
                product1Id = querySnapshot.documents[0].id
                product2Id = querySnapshot.documents[1].id
                product3Id = querySnapshot.documents[2].id

                // to get the products names
                btnProduct1.text = querySnapshot.documents[0].data!!["name"].toString()
                btnProduct2.text = querySnapshot.documents[1].data!!["name"].toString()
                btnProduct3.text = querySnapshot.documents[2].data!!["name"].toString()

                // to change text background color
                btnProduct1.setBackgroundColor(Color.parseColor("#F44336"))
                btnProduct2.setBackgroundColor(Color.parseColor("#F44336"))
                btnProduct3.setBackgroundColor(Color.parseColor("#F44336"))
            }
            .addOnFailureListener { exception ->
                Log.e("abd", exception.message!!)
            }

        btnProduct1.setOnClickListener {
            goToDetails("food", product1Id)
        }

        btnProduct2.setOnClickListener {
            goToDetails("food", product2Id)
        }

        btnProduct3.setOnClickListener {
            goToDetails("food", product3Id)
        }
    }

    // for laptops category
    private fun getLaptopsCategory() {
        db.collection("laptops")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // to store the products id
                product1Id = querySnapshot.documents[0].id
                product2Id = querySnapshot.documents[1].id
                product3Id = querySnapshot.documents[2].id

                // to get the products names
                btnProduct1.text = querySnapshot.documents[0].data!!["name"].toString()
                btnProduct2.text = querySnapshot.documents[1].data!!["name"].toString()
                btnProduct3.text = querySnapshot.documents[2].data!!["name"].toString()

                // to change text background color
                btnProduct1.setBackgroundColor(Color.parseColor("#03A9F4"))
                btnProduct2.setBackgroundColor(Color.parseColor("#03A9F4"))
                btnProduct3.setBackgroundColor(Color.parseColor("#03A9F4"))
            }
            .addOnFailureListener { exception ->
                Log.e("abd", exception.message!!)
            }

        btnProduct1.setOnClickListener {
            goToDetails("laptops", product1Id)
        }

        btnProduct2.setOnClickListener {
            goToDetails("laptops", product2Id)
        }

        btnProduct3.setOnClickListener {
            goToDetails("laptops", product3Id)
        }

    }

    // for phones category
    private fun getPhonesCategory() {
        db.collection("phones")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // to store the products id
                product1Id = querySnapshot.documents[0].id
                product2Id = querySnapshot.documents[1].id
                product3Id = querySnapshot.documents[2].id

                // to get the products names
                btnProduct1.text = querySnapshot.documents[0].data!!["name"].toString()
                btnProduct2.text = querySnapshot.documents[1].data!!["name"].toString()
                btnProduct3.text = querySnapshot.documents[2].data!!["name"].toString()

                // to change text background color
                btnProduct1.setBackgroundColor(Color.parseColor("#4CAF50"))
                btnProduct2.setBackgroundColor(Color.parseColor("#4CAF50"))
                btnProduct3.setBackgroundColor(Color.parseColor("#4CAF50"))
            }
            .addOnFailureListener { exception ->
                Log.e("abd", exception.message!!)
            }

        btnProduct1.setOnClickListener {
            goToDetails("phones", product1Id)
        }

        btnProduct2.setOnClickListener {
            goToDetails("phones", product2Id)
        }

        btnProduct3.setOnClickListener {
            goToDetails("phones", product3Id)
        }

    }

    // to to move to "DetailsActivity"
    private fun goToDetails(category: String, productId: String) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("category", category)
        intent.putExtra("product", productId)
        startActivity(intent)
    }

    // this function to track the ProductsActivity by google analytics
    private fun trackScreen() {
        val parameters = Bundle().apply {
            this.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Category Products Page")
            this.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ProductsActivity")
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

        val data = mapOf("time" to time, "userId" to 1, "pageName" to "Products Page")

        db.collection("time").document("ProductsPage")
            .update(data)
            .addOnSuccessListener {
                Log.e("abd", "Task Complete successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("abd", exception.message!!)
            }
    }
}
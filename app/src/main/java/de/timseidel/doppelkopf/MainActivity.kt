package de.timseidel.doppelkopf

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.databinding.ActivityMainBinding
import de.timseidel.doppelkopf.util.Logging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_session_history, R.id.navigation_finances
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //testFireStore()
    }

    private fun testFireStore(){
        val db = Firebase.firestore

        val player = hashMapOf(
            "name" to "Tims"
        )

        db.collection("users").add(player).addOnSuccessListener {
            documentReference -> Logging.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener {
            e -> Logging.e("Firestore", "Error adding document", e)
        }
    }
}
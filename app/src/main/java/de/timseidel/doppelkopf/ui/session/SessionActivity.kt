package de.timseidel.doppelkopf.ui.session

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivitySessionBinding

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation(){
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_session)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_game_creation, R.id.navigation_game_history, R.id.navigation_session_statistic
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
package de.timseidel.doppelkopf.ui.session

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivitySessionBinding
import de.timseidel.doppelkopf.util.DokoShortAccess

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_session, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_show_group_code) {
            showGroupCode()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGroupCode(){
        val groupCode = DokoShortAccess.getGroupCtrl().getGroup().code
        AlertDialog.Builder(this)
            .setTitle("Gruppencode")
            .setMessage("Der Gruppencode ist: $groupCode")
            .setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
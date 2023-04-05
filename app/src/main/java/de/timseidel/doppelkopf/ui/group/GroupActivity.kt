package de.timseidel.doppelkopf.ui.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivityMainBinding
import de.timseidel.doppelkopf.util.DokoShortAccess

class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_session_history,
                R.id.navigation_group_statistic,
                R.id.navigation_member_ranking,
                R.id.navigation_finances
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_group_overview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_clear_default_group) {
            clearCurrentGroupId()
            resetActivities()
            return true
        } else if (item.itemId == R.id.menu_item_reset_group_statistics) {
            DokoShortAccess.getStatsCtrl().reset()

            showSwitchTabsToSeeChangesDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSwitchTabsToSeeChangesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Statistikupdate")
        builder.setMessage("Die Statistiken werden geupdated, sobald du einen Tab wechselst (und zurück).")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun clearCurrentGroupId() {
        val sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().remove(getString(R.string.shared_preferences_group_id_key)).apply()
    }

    private fun resetActivities() {
        val intent = Intent(this, JoinGroupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
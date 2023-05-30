package de.timseidel.doppelkopf.ui.session

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivitySessionBinding
import de.timseidel.doppelkopf.export.CSVGameHistoryExporter
import de.timseidel.doppelkopf.util.DokoShortAccess

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarMenu()
        setupBottomNavigation()
    }

    private fun setupToolbarMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_session, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                if (item.itemId == R.id.menu_item_session_show_group_code) {
                    showGroupCode()
                    return true
                } else if (item.itemId == R.id.menu_session_export_csv) {
                    exportSessionToCSV()
                    return true
                }
                return false
            }
        })
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_session)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_game_creation,
                R.id.navigation_game_history,
                R.id.navigation_session_statistic
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun exportSessionToCSV() {
        val exporter = CSVGameHistoryExporter()
        val content = exporter.exportGameHistory(DokoShortAccess.getGameCtrl().getGames())
        copyToClipboard(content)
    }

    private fun copyToClipboard(content: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Doppelkopfabend als CSV", content)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            this,
            "Doppelkopfabend als CSV in die Zwischenablage kopiert",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showGroupCode() {
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
package de.timseidel.doppelkopf.ui.group

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivityMainBinding
import de.timseidel.doppelkopf.ui.group.creation.GroupCreationActivity
import de.timseidel.doppelkopf.ui.group.settings.GroupSettingsActivity
import de.timseidel.doppelkopf.util.DokoShortAccess

class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        val group = DokoShortAccess.getGroupCtrl().getGroup()
        supportActionBar?.title = "[${group.code}] ${group.name}"

        val isGroupNewlyCreated = intent.getBooleanExtra(GroupCreationActivity.KEY_GROUP_NEWLY_CREATED_FLAG, false);
        if (isGroupNewlyCreated) {
            showShareCodeDialog()
        }
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_session_history,
                R.id.navigation_group_statistic,
                R.id.navigation_member_ranking
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_group_overview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private val startSettingsForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            DokoShortAccess.getStatsCtrl().invalidate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_clear_default_group -> {
                clearCurrentGroupId()
                resetActivities()
                return true
            }
            R.id.menu_item_group_show_group_code -> {
                shareShareCode()
                return true
            }
            R.id.menu_item_group_group_settings -> {
                val intent = Intent(this, GroupSettingsActivity::class.java)
                startSettingsForResult.launch(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun clearCurrentGroupId() {
        val sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().remove(getString(R.string.shared_preferences_group_id_key)).apply()
    }

    private fun resetActivities() {
        DokoShortAccess.getStatsCtrl().reset()

        val intent = Intent(this, JoinGroupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun showShareCodeDialog() {
        val builder = AlertDialog.Builder(this)
        val shareCodeView = layoutInflater.inflate(R.layout.dialog_share_code, null)

        val tvShareCode = shareCodeView.findViewById<TextView>(R.id.tv_share_code)
        tvShareCode.text = formatShareCode(DokoShortAccess.getGroupCtrl().getGroup().code)

        val dialog = builder.setView(shareCodeView).create()

        val btnCloseDialog = shareCodeView.findViewById<TextView>(R.id.btn_share_code_dialog_close)
        btnCloseDialog.setOnClickListener {
            Log.d("SessionActivity", "Close dialog")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun shareShareCode() {
        val groupCode = DokoShortAccess.getGroupCtrl().getGroup().code
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, "Gruppencode")
            putExtra(Intent.EXTRA_TEXT, groupCode)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Gruppencode teilen")
        startActivity(shareIntent)
    }

    private fun formatShareCode(code: String): String {
        return when (code.length) {
            6 -> {
                code.substring(0, 3) + " " + code.substring(3)
            }

            8 -> {
                code.substring(0, 2) + " " + code.substring(2, 5) + " " + code.substring(5)
            }

            else -> {
                code
            }
        }
    }
}
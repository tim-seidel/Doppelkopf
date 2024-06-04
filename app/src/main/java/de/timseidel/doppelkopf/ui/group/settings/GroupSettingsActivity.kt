package de.timseidel.doppelkopf.ui.group.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.databinding.ActivityGroupSettingsBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.util.DokoShortAccess

class GroupSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupSettingsBinding

    var isBockrundeEnabled: Boolean =
        DokoShortAccess.getSettingsCtrl().getSettings().isBockrundeEnabled

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBockrundenCheckbox()
        setupButtons()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_CANCELED)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        setTitle(R.string.title_group_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupBockrundenCheckbox() {
        binding.cbGroupSettingsBockrundenEnable.isChecked = isBockrundeEnabled
        binding.cbGroupSettingsBockrundenEnable.setOnCheckedChangeListener { _, isChecked ->
            isBockrundeEnabled = isChecked
        }
    }

    private fun setupButtons() {
        binding.btnSaveGroupSettings.setOnClickListener {
            saveSettings()
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun saveSettings() {
        val settings = DokoShortAccess.getSettingsCtrl().getSettings()
        settings.isBockrundeEnabled = isBockrundeEnabled

        val db = Firebase.firestore
        val firebase = DoppelkopfDatabase()
        firebase.setFirestore(db)
        firebase.storeGroupSettings(DokoShortAccess.getGroupCtrl().getGroup(), settings)
    }
}
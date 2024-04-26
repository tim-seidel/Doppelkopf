package de.timseidel.doppelkopf.ui.session.gamecreation

import android.content.Context
import android.media.Image
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Faction

class PlayerFactionSelectView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var tvPlayerName: TextView
    private lateinit var btnSelectRe: ImageButton
    private lateinit var btnSelectContra: ImageButton

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_player_faction_select, this)
        findViews()

        applyAttributes(attrs)
    }

    private fun findViews() {
        tvPlayerName = findViewById(R.id.tv_player_faction_select_name)
        btnSelectRe = findViewById(R.id.btn_faction_re)
        btnSelectContra = findViewById(R.id.btn_faction_contra)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PlayerFactionSelectView)
        try {
            val playerName = ta.getString(R.styleable.PlayerFactionSelectView_playerName) ?: ""
            setPlayerName(playerName)
        } finally {
            ta.recycle()
        }
    }

    fun setPlayerName(name: String) {
        tvPlayerName.text = name
    }

    fun setFaction(faction: Faction) {
        when (faction) {
            Faction.RE -> setRe()
            Faction.CONTRA -> setContra()
            Faction.NONE -> resetFaction()
        }
    }

    private fun setRe() {
        applyFactionButtonColors(btnSelectRe, R.color.white, R.color.primary)
        applyFactionButtonColors(btnSelectContra, R.color.secondary_light, R.color.white)
    }

    private fun setContra() {
        applyFactionButtonColors(btnSelectRe, R.color.primary_light, R.color.white)
        applyFactionButtonColors(btnSelectContra, R.color.white, R.color.secondary)
    }


    private fun resetFaction() {
        applyFactionButtonColors(btnSelectRe, R.color.primary_light, R.color.white)
        applyFactionButtonColors(btnSelectContra, R.color.secondary_light, R.color.white)
    }

    private fun applyFactionButtonColors(btnFaction: ImageButton, foregroundColorCode: Int, backgroundColorCode: Int){
        btnFaction.setColorFilter(ContextCompat.getColor(btnFaction.context, foregroundColorCode))
        btnFaction.background.setTint(ContextCompat.getColor(btnFaction.context, backgroundColorCode))
    }
}
package de.timseidel.doppelkopf.ui.session.gamecreation

import android.content.Context
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

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_player_faction_select, this)

        findViews()

        val ta = context.obtainStyledAttributes(attrs, R.styleable.PlayerFactionSelectView)
        try {
            val playerName = ta.getString(R.styleable.PlayerFactionSelectView_playerName) ?: ""
            setPlayerName(playerName)
        } finally {
            ta.recycle()
        }
    }

    private lateinit var tvPlayerName: TextView
    private lateinit var btnSelectRe: ImageButton
    private lateinit var btnSelectContra: ImageButton

    private fun findViews() {
        tvPlayerName = findViewById(R.id.tv_player_faction_select_name)
        btnSelectRe = findViewById(R.id.btn_faction_re)
        btnSelectContra = findViewById(R.id.btn_faction_contra)
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
        btnSelectRe.setColorFilter(ContextCompat.getColor(btnSelectRe.context, R.color.primary))
        setFactionPassive(btnSelectContra)
    }

    private fun setContra() {
        setFactionPassive(btnSelectRe)
        btnSelectContra.setColorFilter(
            ContextCompat.getColor(
                btnSelectContra.context,
                R.color.secondary
            )
        )
    }

    private fun resetFaction() {
        setFactionPassive(btnSelectRe)
        setFactionPassive(btnSelectContra)
    }

    private fun setFactionPassive(btn: ImageButton) {
        btn.setColorFilter(ContextCompat.getColor(btn.context, R.color.gray_400))
    }
}
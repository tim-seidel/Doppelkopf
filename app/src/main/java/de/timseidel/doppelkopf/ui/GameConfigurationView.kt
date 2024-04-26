package de.timseidel.doppelkopf.ui

import android.content.Context

import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.ui.session.gamecreation.GameConfiguration
import de.timseidel.doppelkopf.ui.session.gamecreation.PlayerFactionSelectAdapter
import de.timseidel.doppelkopf.ui.session.gamecreation.TackenCounterView
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess

class GameConfigurationView constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {


    private lateinit var rvPlayerFactionSelect: RecyclerView
    private lateinit var tcTackenCounter: TackenCounterView
    private lateinit var btnWinningFactionRe: Button
    private lateinit var btnWinningFactionContra: Button
    private lateinit var cbIsBockrunde: CheckBox

    private lateinit var playerFactionSelectAdapter: PlayerFactionSelectAdapter
    private var gameConfigurationChangeListener: GameConfigurationChangeListener? = null

    interface GameConfigurationChangeListener {
        fun onTackenCountChanged(tackenCount: Int)
        fun onWinningFactionChanged(winningFaction: Faction)
        fun onPlayerFactionChanged(player: Player, faction: Faction)
        fun onBockrundeChanged(isBockrunde: Boolean)
    }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_configuration, this)
        findViews()

        applyAttributes(attrs)

        setupPlayerFactionSelectList()
        setupFactionButtons()
        setupTackenCounter()
        setupBockrundeInput()
    }

    fun setGameConfigurationChangeListener(listener: GameConfigurationChangeListener?) {
        this.gameConfigurationChangeListener = listener
    }

    private fun findViews() {
        rvPlayerFactionSelect = findViewById(R.id.rv_game_creation_player_list)
        tcTackenCounter = findViewById(R.id.layout_tacken_counter)
        btnWinningFactionRe = findViewById(R.id.btn_winner_re)
        btnWinningFactionContra = findViewById(R.id.btn_winner_contra)
        cbIsBockrunde = findViewById(R.id.cb_is_bockrunde)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameConfigurationView)
        try {
        } finally {
            ta.recycle()
        }
    }

    fun setGameConfiguration(gameConfiguration: GameConfiguration) {
        setTackenCount(gameConfiguration.tackenCount)
        setIsBockrunde(gameConfiguration.isBockrunde)
        setWinningFaction(gameConfiguration.winningFaction)
        setPlayerFactionList(gameConfiguration.playerFactionList)
    }

    fun resetGameConfiguration() {
        setWinningFaction(Faction.NONE)
        setTackenCount(0)
        setIsBockrunde(false)
        doPlayerFactionReset()
    }

    private fun setupPlayerFactionSelectList() {
        val playerAndFactions: List<PlayerAndFaction> =
            DokoShortAccess.getPlayerCtrl().getPlayersAsFaction()

        playerFactionSelectAdapter = PlayerFactionSelectAdapter(
            playerAndFactions.toMutableList(),
            object : PlayerFactionSelectAdapter.PlayerFactionClickListener {
                override fun onFactionUpdate(player: Player, newFaction: Faction) {
                    gameConfigurationChangeListener?.onPlayerFactionChanged(
                        player,
                        newFaction
                    )
                }
            }
        )

        val dp4 = Converter.convertDpToPixels(4f, rvPlayerFactionSelect.context)
        rvPlayerFactionSelect.adapter = playerFactionSelectAdapter
        rvPlayerFactionSelect.layoutManager = GridLayoutManager(context, 2)
        rvPlayerFactionSelect.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))
    }

    private fun setupFactionButtons() {
        btnWinningFactionRe.setOnClickListener {
            gameConfigurationChangeListener?.onWinningFactionChanged(Faction.RE)
        }
        btnWinningFactionContra.setOnClickListener {
            gameConfigurationChangeListener?.onWinningFactionChanged(Faction.CONTRA)
        }
    }

    private fun setupTackenCounter() {
        tcTackenCounter.setTackenChangeListener(object : TackenCounterView.TackenChangeListener {
            override fun onTackenChanged(count: Int) {
                gameConfigurationChangeListener?.onTackenCountChanged(count)
            }
        })
    }

    private fun setupBockrundeInput() {
        cbIsBockrunde.setOnCheckedChangeListener { _, isChecked ->
            gameConfigurationChangeListener?.onBockrundeChanged(isChecked)
        }
    }


    fun setTackenCount(tackenCount: Int) {
        tcTackenCounter.setTackenCount(tackenCount)
    }

    fun setWinningFaction(winningFaction: Faction) {
        when (winningFaction) {
            Faction.RE -> {
                setButtonColor(btnWinningFactionRe, R.color.primary)
                setButtonColor(btnWinningFactionContra, R.color.secondary_light)
            }

            Faction.CONTRA -> {
                setButtonColor(btnWinningFactionRe, R.color.primary_light)
                setButtonColor(btnWinningFactionContra, R.color.secondary)
            }

            Faction.NONE -> {
                setButtonColor(btnWinningFactionRe, R.color.primary_light)
                setButtonColor(btnWinningFactionContra, R.color.secondary_light)
            }
        }
    }


    fun setIsBockrunde(isBockrunde: Boolean) {
        cbIsBockrunde.isChecked = isBockrunde
    }

    fun setPlayerFaction(player: Player, faction: Faction) {
        playerFactionSelectAdapter.updatePlayerFaction(player, faction)
    }

    fun doPlayerFactionReset() {
        playerFactionSelectAdapter.resetPlayerFactions()
    }

    fun setPlayerFactionList(playerFactionList: List<PlayerAndFaction>) {
        playerFactionSelectAdapter.updatePlayerFactionList(playerFactionList)
    }

    private fun setButtonColor(btn: Button, colorResId: Int) {
        btn.background.setTint(ContextCompat.getColor(btn.context, colorResId))
    }
}
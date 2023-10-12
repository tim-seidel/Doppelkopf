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
import de.timseidel.doppelkopf.model.Game
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

    private var playerFactionList: List<PlayerAndFaction> = emptyList()
    private var winningFaction: Faction = Faction.NONE
    private var tackenCount: Int = 0
    private var isBockrunde: Boolean = false

    interface GameConfigurationChangeListener {
        fun onTackenCountChanged(tackenCount: Int)
        fun onWinningFactionChanged(winningFaction: Faction)
        fun onPlayerFactionChanged(player: Player, faction: Faction)
    }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_configuration, this)
        findViews()

        applyAttributes(attrs)

        setUpPlayerFactionSelectList()
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
        this.playerFactionList = gameConfiguration.playerFactionList
        this.winningFaction = gameConfiguration.winningFaction
        this.tackenCount = gameConfiguration.tackenCount
        this.isBockrunde = gameConfiguration.isBockrunde

        applyGameConfiguration()
    }

    fun resetGameConfiguration() {
        playerFactionList = emptyList()
        winningFaction = Faction.NONE
        tackenCount = 0
        isBockrunde = false

        applyGameConfiguration()
    }

    private fun setUpPlayerFactionSelectList() {
        val playerAndFactions: List<PlayerAndFaction> =
            DokoShortAccess.getPlayerCtrl().getPlayersAsFaction()
        playerFactionList = playerAndFactions.toList()

        playerFactionSelectAdapter = PlayerFactionSelectAdapter(
            playerAndFactions,
            object : PlayerFactionSelectAdapter.PlayerFactionClickListener {
                override fun onFactionUpdate(player: Player, newFaction: Faction) {
                    playerFactionList.find { it.player == player }?.faction = newFaction

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
            winningFaction = Faction.RE
            applyWinningFaction()

            gameConfigurationChangeListener?.onWinningFactionChanged(Faction.RE)
        }
        btnWinningFactionContra.setOnClickListener {
            winningFaction = Faction.CONTRA
            applyWinningFaction()

            gameConfigurationChangeListener?.onWinningFactionChanged(Faction.CONTRA)
        }
    }

    private fun setupTackenCounter() {
        tcTackenCounter.setTackenChangeListener(object : TackenCounterView.TackenChangeListener {
            override fun onTackenChanged(count: Int) {
                tackenCount = count

                gameConfigurationChangeListener?.onTackenCountChanged(count)
            }
        })
    }

    private fun setupBockrundeInput() {
        cbIsBockrunde.setOnCheckedChangeListener { _, isChecked ->
            isBockrunde = isChecked
        }
    }

    private fun applyGameConfiguration() {
        applyWinningFaction()
        applyTackenCount()
        applyIsBockrunde()
    }

    private fun applyWinningFaction() {
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

    private fun applyTackenCount() {
        tcTackenCounter.setTackenCount(tackenCount)
    }

    private fun applyIsBockrunde() {
        cbIsBockrunde.isChecked = isBockrunde
    }

    private fun setButtonColor(btn: Button, colorResId: Int) {
        btn.background.setTint(ContextCompat.getColor(btn.context, colorResId))
    }
}
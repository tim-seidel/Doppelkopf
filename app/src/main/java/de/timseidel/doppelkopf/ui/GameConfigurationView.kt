package de.timseidel.doppelkopf.ui

import android.app.AlertDialog
import android.content.Context

import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.GameTypeHelper
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.MemberAndFaction
import de.timseidel.doppelkopf.ui.session.gamecreation.GameConfiguration
import de.timseidel.doppelkopf.ui.session.gamecreation.MemberFactionSelectAdapter
import de.timseidel.doppelkopf.ui.session.gamecreation.TackenCounterView
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess

class GameConfigurationView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {


    private lateinit var rvMemberFactionSelect: RecyclerView
    private lateinit var tcTackenCounter: TackenCounterView
    private lateinit var btnWinningFactionRe: Button
    private lateinit var btnWinningFactionContra: Button
    private lateinit var cbIsBockrunde: CheckBox
    private lateinit var spGameType: Spinner
    private lateinit var tvGameResultTitle: TextView
    private lateinit var tvGameFeaturesTitle: TextView
    private lateinit var tvGameTypeErrorMessage: TextView

    private lateinit var memberFactionSelectAdapter: MemberFactionSelectAdapter
    private var gameConfigurationChangeListener: GameConfigurationChangeListener? = null

    interface GameConfigurationChangeListener {
        fun onTackenCountChanged(tackenCount: Int)
        fun onWinningFactionChanged(winningFaction: Faction)
        fun onMemberFactionChanged(member: Member, faction: Faction)
        fun onBockrundeChanged(isBockrunde: Boolean)
        fun onGameTypeChanged(gameType: GameType)
    }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_game_configuration, this)
        findViews()

        applyAttributes(attrs)

        setupMemberFactionSelectList()
        setupFactionButtons()
        setupTackenCounter()
        setupBockrundeInput()
        setupGameTypeSpinner()
        setupTitleHelp()
    }

    fun setGameConfigurationChangeListener(listener: GameConfigurationChangeListener?) {
        this.gameConfigurationChangeListener = listener
    }

    private fun findViews() {
        rvMemberFactionSelect = findViewById(R.id.rv_game_creation_member_list)
        tcTackenCounter = findViewById(R.id.layout_tacken_counter)
        btnWinningFactionRe = findViewById(R.id.btn_winner_re)
        btnWinningFactionContra = findViewById(R.id.btn_winner_contra)
        cbIsBockrunde = findViewById(R.id.cb_is_bockrunde)
        spGameType = findViewById(R.id.sp_game_type)
        tvGameResultTitle = findViewById(R.id.tv_game_creation_title_result)
        tvGameFeaturesTitle = findViewById(R.id.tv_game_creation_title_features)
        tvGameTypeErrorMessage = findViewById(R.id.tv_game_creation_gametype_error_message)
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
        setMemberFactionList(gameConfiguration.memberFactionList)
        setGameType(gameConfiguration.gameType)
    }

    fun resetGameConfiguration() {
        setWinningFaction(Faction.NONE)
        setTackenCount(0)
        setIsBockrunde(false)
        doMemberFactionReset()
        setGameType(GameType.NORMAL)
    }

    private fun setupMemberFactionSelectList() {
        val memberAndFactions: List<MemberAndFaction> = DokoShortAccess.getMemberCtrl()
            .getMembersAsFaction(DokoShortAccess.getSessionCtrl().getSession().members)

        memberFactionSelectAdapter = MemberFactionSelectAdapter(
            memberAndFactions.toMutableList(),
            object : MemberFactionSelectAdapter.MemberFactionClickListener {
                override fun onFactionUpdate(member: Member, newFaction: Faction) {
                    gameConfigurationChangeListener?.onMemberFactionChanged(
                        member,
                        newFaction
                    )
                }
            }
        )

        val dp4 = Converter.convertDpToPixels(4f, rvMemberFactionSelect.context)
        rvMemberFactionSelect.adapter = memberFactionSelectAdapter
        rvMemberFactionSelect.layoutManager = GridLayoutManager(context, 2)
        rvMemberFactionSelect.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))
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
        val settings = DokoShortAccess.getSettingsCtrl().getSettings()

        if (settings.isBockrundeEnabled) {
            cbIsBockrunde.setOnCheckedChangeListener { _, isChecked ->
                gameConfigurationChangeListener?.onBockrundeChanged(isChecked)
            }
        }
        cbIsBockrunde.visibility = if (settings.isBockrundeEnabled) View.VISIBLE else View.GONE
    }

    private fun setupGameTypeSpinner() {
        val gameTypeStrings = GameTypeHelper.getStringList()

        val adapter = ArrayAdapter(context, R.layout.view_game_type_spinner, gameTypeStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGameType.adapter = adapter

        spGameType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val gameType = GameTypeHelper.getGameTypeByString(gameTypeStrings[position])
                gameConfigurationChangeListener?.onGameTypeChanged(gameType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //gameConfigurationChangeListener?.onGameTypeChanged(GameType.NORMAL)
            }
        }
    }

    private fun setupTitleHelp() {
        tvGameResultTitle.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.game_result)
                .setMessage(R.string.game_result_help)
                .setPositiveButton(R.string.okay, null)
                .show()
        }

        tvGameFeaturesTitle.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.game_features)
                .setMessage(R.string.game_features_help)
                .setPositiveButton(R.string.okay, null)
                .show()
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

    fun setMemberFaction(member: Member, faction: Faction) {
        memberFactionSelectAdapter.updateMemberFaction(member, faction)
    }

    fun doMemberFactionReset() {
        memberFactionSelectAdapter.resetMemberFactions()
    }

    fun setMemberFactionList(memberFactionList: List<MemberAndFaction>) {
        memberFactionSelectAdapter.updateMemberFactionList(memberFactionList)
    }

    fun setGameType(gameType: GameType) {
        spGameType.setSelection(GameTypeHelper.getIntByGameType(gameType))
    }

    fun setGameTypeErrorMessage(message: String) {
        tvGameTypeErrorMessage.text = message
        if (message.isNotEmpty()) {
            tvGameTypeErrorMessage.visibility = View.VISIBLE
        } else {
            tvGameTypeErrorMessage.visibility = View.GONE
        }
    }

    private fun setButtonColor(btn: Button, colorResId: Int) {
        btn.background.setTint(ContextCompat.getColor(btn.context, colorResId))
    }
}
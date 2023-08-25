package de.timseidel.doppelkopf.ui.session.gamecreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.FragmentGameCreationBinding
import de.timseidel.doppelkopf.db.DoppelkopfDatabase
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.ui.EditTextListener
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.VersusBarView
import de.timseidel.doppelkopf.ui.util.Converter
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.Logging

class GameCreationFragment : Fragment() {

    private var _binding: FragmentGameCreationBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvPlayerFactionSelect: RecyclerView
    private lateinit var tcTackenCounter: TackenCounterView
    private lateinit var ibDecreaseTacken: ImageButton
    private lateinit var ibIncreaseTacken: ImageButton
    private lateinit var btnWinningFactionRe: Button
    private lateinit var btnWinningFactionContra: Button
    private lateinit var etGameScore: EditText
    private lateinit var vbVersusBarScore: VersusBarView
    private lateinit var cbIsBockrunde: CheckBox
    private lateinit var btnSaveGame: Button

    private lateinit var playerFactionSelectAdapter: PlayerFactionSelectAdapter
    private val viewModel: GameCreationViewModel = GameCreationViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameCreationBinding.inflate(inflater, container, false)

        viewModel.playerFactionList =
            DoppelkopfManager.getInstance().getSessionController().getPlayerController()
                .getPlayersAsFaction()


        findViews()
        setupToolbar()
        setUpPlayerFactionSelectList()
        setupButtons()
        setupPointsInput()
        setupBockrundeInput()

        checkSaveGameButtonEnabled()

        return binding.rootGameCreation
    }

    private fun findViews() {
        rvPlayerFactionSelect = binding.rvGameCreationPlayerList
        tcTackenCounter = binding.layoutTackenCounter
        ibDecreaseTacken = tcTackenCounter.findViewById(R.id.ib_decrease_tacken)
        ibIncreaseTacken = tcTackenCounter.findViewById(R.id.ib_increase_tacken)
        btnWinningFactionRe = binding.btnWinnerRe
        btnWinningFactionContra = binding.btnWinnerContra
        etGameScore = binding.etGamePoints
        vbVersusBarScore = binding.versusBarPoints
        cbIsBockrunde = binding.cbIsBockrunde
        btnSaveGame = binding.btnSaveGame
    }

    private fun setupToolbar(){
        (activity as AppCompatActivity).supportActionBar?.title =
            DokoShortAccess.getSessionCtrl().getSession().name

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupButtons() {
        ibDecreaseTacken.setOnClickListener {
            viewModel.tackenCount -= 1
            applyTacken()
        }
        ibIncreaseTacken.setOnClickListener {
            viewModel.tackenCount += 1
            applyTacken()
        }

        btnWinningFactionRe.setOnClickListener {
            viewModel.winningFaction = Faction.RE
            applyWinningFaction()
        }
        btnWinningFactionContra.setOnClickListener {
            viewModel.winningFaction = Faction.CONTRA
            applyWinningFaction()
        }

        btnSaveGame.setOnClickListener {
            onCreateGameClicked()
        }
    }

    private fun setupPointsInput() {
        etGameScore.addTextChangedListener(object : EditTextListener() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                try {
                    viewModel.gameScore = input.toInt()
                } catch (_: Exception) {
                    viewModel.gameScore = 0
                }

                applyGameScore()
            }
        })
    }

    private fun setupBockrundeInput() {
        cbIsBockrunde.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isBockrunde = isChecked
        }
    }

    private fun setUpPlayerFactionSelectList() {
        playerFactionSelectAdapter = PlayerFactionSelectAdapter(
            viewModel.playerFactionList,
            object : PlayerFactionSelectAdapter.PlayerFactionClickListener {
                override fun onFactionClicked(position: Int, selectedFaction: Faction): Faction {
                    val player = viewModel.playerFactionList[position]
                    val newFaction = when (player.faction) {
                        Faction.RE -> if (selectedFaction == Faction.CONTRA) Faction.CONTRA else Faction.NONE
                        Faction.CONTRA -> if (selectedFaction == Faction.RE) Faction.RE else Faction.NONE
                        Faction.NONE -> selectedFaction
                    }

                    viewModel.playerFactionList[position].faction = newFaction

                    checkSaveGameButtonEnabled()

                    return newFaction
                }
            })

        val dp4 = Converter.convertDpToPixels(4f, rvPlayerFactionSelect.context)
        rvPlayerFactionSelect.adapter = playerFactionSelectAdapter
        rvPlayerFactionSelect.layoutManager = GridLayoutManager(binding.rootGameCreation.context, 2)
        rvPlayerFactionSelect.addItemDecoration(RecyclerViewMarginDecoration(dp4, dp4))
    }

    private fun applyViewModel() {
        applyWinningFaction()
        applyTacken()
        applyGameScore()
        applyIsBockrunde()
    }

    private fun applyWinningFaction() {
        when (viewModel.winningFaction) {
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
        checkSaveGameButtonEnabled()
    }

    private fun applyTacken() {
        tcTackenCounter.setCounter(viewModel.tackenCount)
        checkSaveGameButtonEnabled()
    }

    private fun applyIsBockrunde() {
        cbIsBockrunde.isChecked = viewModel.isBockrunde
    }

    private fun setButtonColor(btn: Button, colorResId: Int) {
        btn.background.setTint(ContextCompat.getColor(btn.context, colorResId))
    }

    private fun applyGameScore() {
        vbVersusBarScore.setProgress(viewModel.gameScore)
        vbVersusBarScore.setLeftText(viewModel.gameScore.toString())
        vbVersusBarScore.setRightText((240 - viewModel.gameScore).toString())
        checkSaveGameButtonEnabled()
    }

    private fun checkSaveGameButtonEnabled() {
        val isValid = viewModel.checkIsValid()

        btnSaveGame.isEnabled = isValid
        setButtonColor(btnSaveGame, if (isValid) R.color.neural else R.color.neural_light)
    }

    private fun onCreateGameClicked() {
        if (!viewModel.checkIsValid()) {
            Toast.makeText(
                context,
                getString(R.string.game_creation_error),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val game = DoppelkopfManager.getInstance().getSessionController().getGameController()
                .createGame(
                    viewModel.playerFactionList.map { it.copy() },
                    viewModel.winningFaction,
                    viewModel.gameScore,
                    viewModel.tackenCount,
                    viewModel.isBockrunde,
                    if (GameUtil.isFactionCompositionSolo(viewModel.playerFactionList)) GameType.SOLO else GameType.NORMAL
                )
            DokoShortAccess.getGameCtrl().addGame(game)

            val db = Firebase.firestore

            val firebase = DoppelkopfDatabase()
            firebase.setFirestore(db)

            firebase.storeGameInSession(
                game,
                DokoShortAccess.getSessionCtrl().getSession(),
                DokoShortAccess.getGroupCtrl().getGroup()
            )

            Toast.makeText(
                context,
                getString(R.string.game_saved),
                Toast.LENGTH_LONG
            ).show()

            resetViewModel()
        } catch (e: Exception) {
            Logging.e("GameCreationFragment", "Error creating game", e)
        }
    }

    private fun resetViewModel() {
        viewModel.reset()
        applyViewModel()
        playerFactionSelectAdapter.resetPlayerFactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package de.timseidel.doppelkopf.ui.session.gamecreation

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.timseidel.doppelkopf.R
import de.timseidel.doppelkopf.controller.DoppelkopfManager
import de.timseidel.doppelkopf.databinding.FragmentGameCreationBinding
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.ui.RecyclerViewMarginDecoration
import de.timseidel.doppelkopf.ui.VersusBarView
import de.timseidel.doppelkopf.util.Converter
import de.timseidel.doppelkopf.util.EditTextListener
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

        setupMenu()

        findViews()
        setUpPlayerFactionSelectList()
        setupButtons()
        setupPointsInput()

        return binding.rootGameCreation
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_create_game, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.option_confirm_create_game -> {
                        onCreateGameClicked()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
                    return newFaction
                }
            })

        rvPlayerFactionSelect.adapter = playerFactionSelectAdapter
        rvPlayerFactionSelect.apply {
            layoutManager = GridLayoutManager(binding.rootGameCreation.context, 2)
        }
        rvPlayerFactionSelect.addItemDecoration(
            RecyclerViewMarginDecoration(
                Converter.convertDpToPixels(
                    4f,
                    rvPlayerFactionSelect.context
                ), 2
            )
        )
    }

    private fun applyViewModel() {
        applyWinningFaction()
        applyTacken()
        applyGameScore()
    }

    private fun applyWinningFaction() {
        when (viewModel.winningFaction) {
            Faction.RE -> {
                setButtonColor(btnWinningFactionRe, R.color.teal)
                setButtonColor(btnWinningFactionContra, R.color.deep_purple_accent)
            }
            Faction.CONTRA -> {
                setButtonColor(btnWinningFactionRe, R.color.teal_accent)
                setButtonColor(btnWinningFactionContra, R.color.deep_purple)
            }
            Faction.NONE -> {
                setButtonColor(btnWinningFactionRe, R.color.teal_accent)
                setButtonColor(btnWinningFactionContra, R.color.deep_purple_accent)
            }
        }
    }

    private fun applyTacken() {
        tcTackenCounter.setCounter(viewModel.tackenCount)
    }

    private fun setButtonColor(btn: Button, colorResId: Int) {
        btn.background.setTint(ContextCompat.getColor(btn.context, colorResId))
    }

    private fun applyGameScore() {
        vbVersusBarScore.setProgress(viewModel.gameScore)
        vbVersusBarScore.setLeftText(viewModel.gameScore.toString())
        vbVersusBarScore.setRightText((240 - viewModel.gameScore).toString())
    }

    private fun onCreateGameClicked() {
        Logging.d("$viewModel")
        if (!viewModel.checkIsValid()) {
            Toast.makeText(
                context,
                "Das Spiel wird nicht erstellt, da die Einstellungen nicht gültig sind",
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
                    viewModel.tackenCount
                )
            DoppelkopfManager.getInstance().getSessionController().getGameController().addGame(game)

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
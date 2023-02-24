package de.timseidel.doppelkopf.ui.session.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import de.timseidel.doppelkopf.databinding.FragmentSessionStatisticBinding
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.statistic.PlayerStatisticsCalculator
import de.timseidel.doppelkopf.ui.session.gamehistory.GameHistoryListPlayerHeaderAdapter.OnPlayerClickListener
import de.timseidel.doppelkopf.ui.session.statistic.provider.EmptyStatisticViewProvider
import de.timseidel.doppelkopf.ui.session.statistic.provider.IStatisticViewsProvider
import de.timseidel.doppelkopf.ui.session.statistic.provider.PlayerStatisticViewsProvider
import de.timseidel.doppelkopf.ui.session.statistic.provider.SessionStatisticViewsProvider
import de.timseidel.doppelkopf.util.DokoShortAccess

//TODO: Uebergabeparameter anpassen, Failcheck
class SessionStatisticFragment : Fragment() {

    private val defaultSessionStatsPlayerPlaceholderId = "__default_group"

    private var _binding: FragmentSessionStatisticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSessionStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = DokoShortAccess.getSessionCtrl().getSession().name

        setupPlayerSelect()
        setupStatistics()

        return root
    }

    private fun setupStatistics() {
        setStatistics(
            if (DokoShortAccess.getGameCtrl().getGames().isEmpty())
                EmptyStatisticViewProvider()
            else SessionStatisticViewsProvider()
        )
    }

    private fun setupPlayerSelect() {
        binding.headerStatisticPlayerSelect.setListener(object : OnPlayerClickListener {
            //TODO: Fail save
            override fun onPlayerClicked(player: Player) {
                if (player.id == defaultSessionStatsPlayerPlaceholderId) {
                    setStatistics(SessionStatisticViewsProvider())
                } else {
                    val games = DokoShortAccess.getGameCtrl().getGames()
                    val players = DokoShortAccess.getPlayerCtrl().getPlayers()
                    val stats = PlayerStatisticsCalculator().calculatePlayerStatistic(
                        player,
                        players.filter { p -> p.id != player.id },
                        games
                    )

                    setStatistics(PlayerStatisticViewsProvider(stats))
                }
            }
        })

        val playerDefaultGroupStatisticPlaceholder =
            Player(defaultSessionStatsPlayerPlaceholderId, "Alle")
        val players = DokoShortAccess.getPlayerCtrl().getPlayers().toMutableList()
        players.add(0, playerDefaultGroupStatisticPlaceholder)
        binding.headerStatisticPlayerSelect.setPlayers(players)
    }

    private fun setStatistics(provider: IStatisticViewsProvider) {
        val lvStatistic = binding.lvStatistic

        val statisticItems = provider.getStatisticItems()
        val adapter = SessionStatisticListAdapter(
            requireContext(),
            statisticItems
        )
        lvStatistic.adapter = adapter
    }

/*
private fun setupPlayerPartnerTackenChart(stats: List<PlayerStatistic>, playerIndex: Int) {
    val chartView = binding.chartPlayerPartnerTacken

    val names = mutableListOf<String>()
    val tacken = mutableListOf<String>()
    val wins = mutableListOf<Number>()
    val loss = mutableListOf<Number>()
    stats[playerIndex].partners.values.forEach { p ->
        names.add(p.player.name)
        tacken.add(p.general.total.tacken.toString())
        wins.add(p.general.wins.tacken)
        loss.add(p.general.loss.tacken)
    }

    val options = HIOptions()
    val chart = HIChart()
    chart.type = "bar"
    options.chart = chart

    val title = HITitle()
    title.text = "Tacken mit Partner"
    options.title = title

    val exporting = HIExporting()
    exporting.enabled = false
    options.exporting = exporting

    val xAxisLeft = HIXAxis()
    xAxisLeft.categories = ArrayList(names)
    xAxisLeft.labels = HILabels()
    xAxisLeft.labels.step = 1

    val xAxisRight = HIXAxis()
    xAxisRight.opposite = true
    xAxisRight.reversed = false
    xAxisRight.categories = ArrayList(tacken)
    xAxisRight.linkedTo = 0
    xAxisRight.labels = HILabels()
    xAxisRight.labels.step = 1

    options.xAxis = arrayListOf(xAxisLeft, xAxisRight)

    val plotOptions = HIPlotOptions()
    plotOptions.bar = HIBar()
    plotOptions.bar.stacking = "normal"
    options.plotOptions = plotOptions

    val barWin = HIBar()
    barWin.name = "Bei Siegen"
    barWin.color = HIColor.initWithHexValue("22cc22")
    barWin.data = ArrayList(wins)

    val barLoss = HIBar()
    barLoss.name = "Bei Niederlagen"
    barLoss.color = HIColor.initWithHexValue("cc2222")
    barLoss.data = ArrayList(loss)

    options.series = arrayListOf(barWin, barLoss)
    chartView.options = options
}
 */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package de.timseidel.doppelkopf.ui.session.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.*
import de.timseidel.doppelkopf.databinding.FragmentSessionStatisticBinding
import de.timseidel.doppelkopf.ui.session.statistic.provider.EmptyStatisticViewProvider
import de.timseidel.doppelkopf.ui.session.statistic.provider.PlayerStatisticViewsProvider
import de.timseidel.doppelkopf.ui.session.statistic.provider.SessionStatisticViewsProvider
import de.timseidel.doppelkopf.util.DokoShortAccess

//TODO: Charts vereinfachen, Uebergabeparameter anpassen, Failcheck, Playerauswahl siehe PlayerHistoryHeader
class SessionStatisticFragment : Fragment() {

    private var _binding: FragmentSessionStatisticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSessionStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val lvStatistic = binding.lvStatistic
        val statisticViewProvider =
            if (DokoShortAccess.getGameCtrl().getGames().isEmpty())
                EmptyStatisticViewProvider() else PlayerStatisticViewsProvider()

        val statisticItems = statisticViewProvider.getStatisticItems()
        val adapter = SessionStatisticListAdapter(
            requireContext(),
            statisticItems.toMutableList()
        )
        lvStatistic.adapter = adapter

        return root
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
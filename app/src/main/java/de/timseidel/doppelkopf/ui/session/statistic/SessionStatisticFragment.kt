package de.timseidel.doppelkopf.ui.session.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.*
import de.timseidel.doppelkopf.databinding.FragmentSessionStatisticBinding
import de.timseidel.doppelkopf.model.statistic.PlayerStatistic
import de.timseidel.doppelkopf.model.statistic.PlayerStatisticsCalculator
import de.timseidel.doppelkopf.util.DokoShortAccess
import de.timseidel.doppelkopf.util.Logging


//TODO: Charts vereinfachen, Uebergabeparameter anpassen, Failcheck, Playerauswahl siehe PlayerHiistoryHeader
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

        val stats = PlayerStatisticsCalculator().calculatePlayerStatistic(
            DokoShortAccess.getPlayerCtrl().getPlayers(),
            DokoShortAccess.getGameCtrl().getGames()
        )

        Logging.d(stats.toString())

        setupTackenChart(stats)
        setupWinLossReContraChart(stats)
        setupTackenWinLossReContraChart(stats)

        setStatisticsChartsForPlayer(stats, 0)

        return root
    }

    private fun setStatisticsChartsForPlayer(stats: List<PlayerStatistic>, playerIndex: Int) {
        setupPlayerPartnerWinLossChart(stats, playerIndex)
        setupPlayerOpponentWinLossChart(stats, playerIndex)
        setupPlayerPartnerTackenChart(stats, playerIndex)

    }

    private fun setDefaultHIOptions(options: HIOptions, chartTitle: String, yAxisTitle: String) {
        val title = HITitle()
        title.text = chartTitle
        options.title = title

        val yAxis = HIYAxis()
        yAxis.title = HITitle()
        yAxis.title.text = yAxisTitle
        options.yAxis = arrayListOf(yAxis)

        val credits = HICredits()
        credits.enabled = false
        options.credits = credits

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting
    }

    private fun setupTackenChart(stats: List<PlayerStatistic>) {
        val chartView = binding.chartTacken

        val options = HIOptions()
        setDefaultHIOptions(options, "Tackenverlauf", "Tacken")

        val legend = HILegend()
        legend.layout = "horizontal"
        legend.align = "center"
        legend.verticalAlign = "top"
        options.legend = legend

        val lines = arrayListOf<HILine>()

        stats.forEach { s ->
            val line = HILine()
            line.name = s.player.name
            line.data = ArrayList(s.tackenHistoryAccumulated)

            lines.add(line)
        }

        options.series = ArrayList(lines)

        chartView.options = options
    }

    private fun setupWinLossReContraChart(stats: List<PlayerStatistic>) {
        val chartView = binding.chartWinLoss

        val options = HIOptions()
        setDefaultHIOptions(options, "Siege/Niederlagen", "Spiele")

        val xAxis = HIXAxis()
        xAxis.categories = ArrayList()
        options.xAxis = arrayListOf(xAxis)

        val legend = HILegend()
        legend.layout = "horizontal"
        legend.align = "center"
        legend.verticalAlign = "top"
        legend.enabled = false
        options.legend = legend

        val plotOptions = HIPlotOptions()
        plotOptions.column = HIColumn()
        plotOptions.column.stacking = "normal"
        options.plotOptions = plotOptions

        val colReWin = HIColumn()
        colReWin.name = "Siege Re"
        colReWin.stack = "Siege"
        colReWin.color = HIColor.initWithHexValue("22cc22")
        val colReLoss = HIColumn()
        colReLoss.name = "Niederlagen Re"
        colReLoss.stack = "Niederlagen"
        colReLoss.color = HIColor.initWithRGB(255, 140, 157)
        val colContraWin = HIColumn()
        colContraWin.name = "Siege Contra"
        colContraWin.stack = "Siege"
        colContraWin.color = HIColor.initWithRGB(170, 255, 100)
        val colContraLoss = HIColumn()
        colContraLoss.name = "Niederlagen Contra"
        colContraLoss.stack = "Niederlagen"
        colContraLoss.color = HIColor.initWithRGB(255, 160, 170)

        val reWins = arrayListOf<Number>()
        val reLoss = arrayListOf<Number>()
        val contraWins = arrayListOf<Number>()
        val contraLoss = arrayListOf<Number>()

        stats.forEach { ps ->
            xAxis.categories.add(ps.player.name)

            reWins.add(ps.re.wins.games)
            reLoss.add(ps.re.loss.games)
            contraWins.add(ps.contra.wins.games)
            contraLoss.add(ps.contra.loss.games)
        }

        colReWin.data = reWins
        colReLoss.data = reLoss
        colContraWin.data = contraWins
        colContraLoss.data = contraLoss

        options.series = ArrayList(arrayListOf(colContraWin, colReWin, colContraLoss, colReLoss))
        chartView.options = options
    }

    private fun setupTackenWinLossReContraChart(stats: List<PlayerStatistic>) {
        val chartView = binding.chartTackenWinLoss

        val options = HIOptions()
        setDefaultHIOptions(options, "Tacken Siege/Niederlagen", "Spiele")
        options.yAxis[0].tickInterval = 25 //TODO Dynamisch

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val xAxis = HIXAxis()
        xAxis.categories = ArrayList()
        options.xAxis = arrayListOf(xAxis)

        val legend = HILegend()
        legend.layout = "horizontal"
        legend.align = "center"
        legend.verticalAlign = "top"
        legend.enabled = false
        options.legend = legend

        val plotOptions = HIPlotOptions()
        plotOptions.column = HIColumn()
        plotOptions.column.stacking = "normal"
        options.plotOptions = plotOptions

        val colReWin = HIColumn()
        colReWin.name = "Tacken Re Siege"
        colReWin.stack = "Siege"
        colReWin.color = HIColor.initWithHexValue("22cc22")
        val colReLoss = HIColumn()
        colReLoss.name = "Tacken Niederlagen Re"
        colReLoss.stack = "Niederlagen"
        colReLoss.color = HIColor.initWithHexValue("cc2222")
        val colContraWin = HIColumn()
        colContraWin.name = "Tacken Siege Contra"
        colContraWin.stack = "Siege"
        colContraWin.color = HIColor.initWithHexValue("22cc22")
        val colContraLoss = HIColumn()
        colContraLoss.name = "Tacken Niederlagen Contra"
        colContraLoss.stack = "Niederlagen"
        colContraLoss.color = HIColor.initWithHexValue("cc2222")

        val reWins = arrayListOf<Number>()
        val reLoss = arrayListOf<Number>()
        val contraWins = arrayListOf<Number>()
        val contraLoss = arrayListOf<Number>()

        stats.forEach { ps ->
            xAxis.categories.add(ps.player.name)

            reWins.add(ps.re.wins.tacken)
            reLoss.add(ps.re.loss.tacken)
            contraWins.add(ps.contra.wins.tacken)
            contraLoss.add(ps.contra.loss.tacken)
        }

        colReWin.data = reWins
        colReLoss.data = reLoss
        colContraWin.data = contraWins
        colContraLoss.data = contraLoss

        options.series = ArrayList(arrayListOf(colContraWin, colReWin, colContraLoss, colReLoss))
        chartView.options = options
    }

    private fun setupPlayerPartnerWinLossChart(stats: List<PlayerStatistic>, playerIndex: Int) {
        val chartView = binding.chartPlayerPartnerWinLoss

        val names = mutableListOf<String>()
        val wins = mutableListOf<Number>()
        val loss = mutableListOf<Number>()
        stats[playerIndex].partners.values.forEach { p ->
            names.add(p.player.name)
            wins.add(p.general.wins.games)
            loss.add(-1 * p.general.loss.games)
        }

        val options = HIOptions()
        val chart = HIChart()
        chart.type = "bar"
        options.chart = chart

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val title = HITitle()
        title.text = "Siege/Ndl. mit Partner"
        options.title = title

        val xAxisLeft = HIXAxis()
        xAxisLeft.categories = ArrayList(names)
        xAxisLeft.labels = HILabels()
        xAxisLeft.labels.step = 1

        val xAxisRight = HIXAxis()
        xAxisRight.opposite = true
        xAxisRight.reversed = false
        xAxisRight.categories = ArrayList(names)
        xAxisRight.linkedTo = 0
        xAxisRight.labels = HILabels()
        xAxisRight.labels.step = 1

        options.xAxis = arrayListOf(xAxisLeft, xAxisRight)

        val plotOptions = HIPlotOptions()
        plotOptions.bar = HIBar()
        plotOptions.bar.stacking = "normal"
        options.plotOptions = plotOptions

        val barWin = HIBar()
        barWin.name = "Siege"
        barWin.color = HIColor.initWithHexValue("22cc22")
        barWin.data = ArrayList(wins)

        val barLoss = HIBar()
        barLoss.name = "Niederlagen"
        barLoss.color = HIColor.initWithHexValue("cc2222")
        barLoss.data = ArrayList(loss)

        options.series = arrayListOf(barWin, barLoss)
        chartView.options = options
    }

    private fun setupPlayerOpponentWinLossChart(stats: List<PlayerStatistic>, playerIndex: Int) {
        val chartView = binding.chartPlayerOpponentWinLoss

        val names = mutableListOf<String>()
        val wins = mutableListOf<Number>()
        val loss = mutableListOf<Number>()
        stats[playerIndex].opponents.values.forEach { p ->
            names.add(p.player.name)
            wins.add(p.general.wins.games)
            loss.add(-1 * p.general.loss.games)
        }

        val options = HIOptions()
        val chart = HIChart()
        chart.type = "bar"
        options.chart = chart

        val title = HITitle()
        title.text = "Siege/Ndl. gegen Gegner"
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
        xAxisRight.categories = ArrayList(names)
        xAxisRight.linkedTo = 0
        xAxisRight.labels = HILabels()
        xAxisRight.labels.step = 1

        options.xAxis = arrayListOf(xAxisLeft, xAxisRight)

        val plotOptions = HIPlotOptions()
        plotOptions.bar = HIBar()
        plotOptions.bar.stacking = "normal"
        options.plotOptions = plotOptions

        val barWin = HIBar()
        barWin.name = "Siege"
        barWin.color = HIColor.initWithHexValue("22cc22")
        barWin.data = ArrayList(wins)

        val barLoss = HIBar()
        barLoss.name = "Niederlagen"
        barLoss.color = HIColor.initWithHexValue("cc2222")
        barLoss.data = ArrayList(loss)

        options.series = arrayListOf(barWin, barLoss)
        chartView.options = options
    }

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

    /*
    private fun setupTackenWinLossChart(stats: List<PlayerStatistic>) {
        val chartView = binding.chartTackenWinLoss

        val options = HIOptions()
        setDefaultHIOptions(options, "Tacken Siege/Niederlagen", "Tacken")

        val xAxis = HIXAxis()
        xAxis.categories = ArrayList()
        options.xAxis = arrayListOf(xAxis)

        val legend = HILegend()
        legend.layout = "horizontal"
        legend.align = "center"
        legend.verticalAlign = "top"
        legend.enabled = false
        options.legend = legend

        val colWin = HIColumn()
        colWin.name = "Siege"
        colWin.color = HIColor.initWithHexValue("22cc22")
        val colLoss = HIColumn()
        colLoss.name = "Niederlagen"
        colLoss.color = HIColor.initWithHexValue("cc2222")

        val wins = arrayListOf<Number>()
        val loss = arrayListOf<Number>()

        stats.forEach { ps ->
            xAxis.categories.add(ps.player.name)

            wins.add(ps.general.wins.tacken)
            loss.add(ps.general.loss.tacken)
        }
        colWin.data = wins
        colLoss.data = loss

        options.series = ArrayList(arrayListOf(colWin, colLoss))
        chartView.options = options
    }
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
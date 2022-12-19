package de.timseidel.doppelkopf.ui.session.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.highsoft.highcharts.common.hichartsclasses.*
import de.timseidel.doppelkopf.databinding.FragmentSessionStatisticBinding
import de.timseidel.doppelkopf.model.statistic.PlayerStatisticsCalculator
import de.timseidel.doppelkopf.util.DokoShortAccess


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

        val chartView = binding.chart
        val options = HIOptions()

        val title = HITitle()
        title.text = "Tackenverlauf"
        options.title = title

        val yaxis = HIYAxis()
        yaxis.title = HITitle()
        yaxis.title.text = "Tacken"
        options.yAxis = arrayListOf(yaxis)

        val legend = HILegend()
        legend.layout = "horizontal"
        legend.align = "center"
        legend.verticalAlign = "top"
        options.legend = legend

        val stats = PlayerStatisticsCalculator().calculatePlayerStatistic(
            DokoShortAccess.getPlayerCtrl().getPlayers(),
            DokoShortAccess.getGameCtrl().getGames()
        )

        val tackenLines = arrayListOf<HILine>()
        stats.forEach { s ->
            val line = HILine()
            line.name = s.player.name
            line.data = ArrayList(s.tackenHistoryAccumulated)

            tackenLines.add(line)
        }

        options.series = ArrayList(tackenLines)

        chartView.options = options

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
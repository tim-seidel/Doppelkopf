package de.timseidel.doppelkopf.ui.statistic.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.highsoft.highcharts.common.hichartsclasses.HICredits
import com.highsoft.highcharts.common.hichartsclasses.HIExporting
import com.highsoft.highcharts.common.hichartsclasses.HILegend
import com.highsoft.highcharts.common.hichartsclasses.HILine
import com.highsoft.highcharts.common.hichartsclasses.HIOptions
import com.highsoft.highcharts.common.hichartsclasses.HITitle
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis
import com.highsoft.highcharts.core.HIChartView
import de.timseidel.doppelkopf.ui.util.Converter

class LineChartViewWrapper(private val chartData: LineChartData) : IStatisticViewWrapper {

    data class LineChartData(
        val title: String,
        val yAxisName: String,
        val lineData: List<ChartLineData>,
        val height: Float = 500f
    )

    data class ChartLineData(val name: String, val values: List<Number>)

    override fun getItemType(): Int {
        return IStatisticViewWrapper.ITEM_TYPE_CHART_LINE
    }

    override fun getView(context: Context): View {
        val lineChart = HIChartView(context)
        lineChart.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            Converter.convertDpToPixels(chartData.height, context)
        )
        lineChart.setBackgroundColor(Color.WHITE)

        val options = HIOptions()

        val title = HITitle()
        title.text = chartData.title
        options.title = title

        val yAxis = HIYAxis()
        yAxis.title = HITitle()
        yAxis.title.text = chartData.yAxisName
        options.yAxis = arrayListOf(yAxis)

        val credits = HICredits()
        credits.enabled = false
        options.credits = credits

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val legend = HILegend()
        legend.layout = "horizontal"
        legend.align = "center"
        legend.verticalAlign = "top"
        options.legend = legend

        val lines = arrayListOf<HILine>()
        chartData.lineData.forEach { ld ->
            val line = HILine()
            line.name = ld.name
            line.data = ArrayList(ld.values)
            lines.add(line)
        }

        options.series = ArrayList(lines)
        lineChart.options = options

        return lineChart
    }
}
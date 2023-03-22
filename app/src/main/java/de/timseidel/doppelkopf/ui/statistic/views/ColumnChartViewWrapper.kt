package de.timseidel.doppelkopf.ui.statistic.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.HIColumn
import com.highsoft.highcharts.common.hichartsclasses.HICredits
import com.highsoft.highcharts.common.hichartsclasses.HIExporting
import com.highsoft.highcharts.common.hichartsclasses.HILabels
import com.highsoft.highcharts.common.hichartsclasses.HILegend
import com.highsoft.highcharts.common.hichartsclasses.HIOptions
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions
import com.highsoft.highcharts.common.hichartsclasses.HITitle
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis
import com.highsoft.highcharts.core.HIChartView
import de.timseidel.doppelkopf.ui.util.Converter

//TODO: Legende an/aus
//TODO: Wieder eigenen Titel zwecks Farbanpassung?
class ColumnChartViewWrapper(val data: ColumnChartData) : IStatisticViewWrapper {

    data class ColumnChartData(
        val title: String,
        val xAxisTitle: String,
        val yAxisTitle: String,
        val series: List<ColumnSeriesData>,
        val categories: List<String> = listOf(),
        val height: Float = 300f
    )

    data class ColumnSeriesData(val name: String, val stacks: List<ColumnSeriesStackData>)

    data class ColumnSeriesStackData(
        val name: String,
        val colorHex: String,
        val values: List<Number>
    )

    override fun getItemType(): Int {
        return IStatisticViewWrapper.ITEM_TYPE_CHART_COLUMN
    }

    override fun getView(context: Context): View {
        val columnChart = HIChartView(context)
        columnChart.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            Converter.convertDpToPixels(data.height, context)
        )
        columnChart.setBackgroundColor(Color.WHITE)

        val options = HIOptions()

        val title = HITitle()
        title.text = data.title
        options.title = title

        val legend = HILegend()
        legend.enabled = false
        options.legend = legend

        val yAxis = HIYAxis()
        yAxis.title = HITitle()
        yAxis.title.text = data.yAxisTitle
        options.yAxis = arrayListOf(yAxis)

        val credits = HICredits()
        credits.enabled = false
        options.credits = credits

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val xAxis = HIXAxis()
        xAxis.title = HITitle()
        xAxis.title.text = data.xAxisTitle
        xAxis.categories = ArrayList(data.categories)
        if (data.categories.isNotEmpty()) {
            xAxis.labels = HILabels()
            xAxis.labels.step = 1
        }
        options.xAxis = arrayListOf(xAxis)

        val plotOptions = HIPlotOptions()
        plotOptions.column = HIColumn()
        plotOptions.column.stacking = "normal"
        options.plotOptions = plotOptions

        val columns = arrayListOf<HIColumn>()
        data.series.forEach { series ->
            series.stacks.forEach { stack ->
                val column = HIColumn()
                column.name = stack.name
                column.color = HIColor.initWithHexValue(stack.colorHex)
                column.data = ArrayList(stack.values)
                column.stack = series.name

                columns.add(column)
            }
        }

        options.series = ArrayList(columns)
        columnChart.options = options

        return columnChart
    }
}
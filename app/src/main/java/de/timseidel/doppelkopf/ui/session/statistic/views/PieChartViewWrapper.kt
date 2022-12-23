package de.timseidel.doppelkopf.ui.session.statistic.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.highsoft.highcharts.common.hichartsclasses.*
import com.highsoft.highcharts.core.HIChartView
import de.timseidel.doppelkopf.ui.session.statistic.IStatisticViewWrapper
import de.timseidel.doppelkopf.util.Converter

class PieChartViewWrapper(val pieData: PieChartData) : IStatisticViewWrapper {

    class PieChartData(val title: String, val subTitle: String, val unit: String, val slices: List<PieSliceData>)

    class PieSliceData(val name: String, val value: Number, val colorHex: String)

    override fun getItemType(): Int {
        return IStatisticViewWrapper.ITEM_TYPE_CHART_PIE
    }

    override fun getView(context: Context): View {
        val pieChart = HIChartView(context)
        pieChart.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            Converter.convertDpToPixels(300f, context)
        )
        pieChart.setBackgroundColor(Color.WHITE)

        val options = HIOptions()

        val title = HITitle()
        title.text = pieData.title
        options.title = title

        val subtitle = HISubtitle()
        subtitle.text = pieData.subTitle
        options.subtitle = subtitle

        val credits = HICredits()
        credits.enabled = false
        options.credits = credits

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val plotOptions = HIPlotOptions()
        plotOptions.pie = HIPie()
        plotOptions.pie.innerSize = "50%"
        val dataLabels = HIDataLabels()
        dataLabels.enabled = true
        dataLabels.format = "{point.name}: {point.y}"
        plotOptions.pie.dataLabels = arrayListOf(dataLabels)
        options.plotOptions = plotOptions

        val slices = arrayListOf<HashMap<String, Any>>()
        pieData.slices.forEach { s ->
            val sliceDataMap = HashMap<String, Any>()
            sliceDataMap["name"] = s.name
            sliceDataMap["y"] = s.value
            sliceDataMap["color"] = s.colorHex
            slices.add(sliceDataMap)
        }

        val pie = HIPie()
        pie.name = pieData.unit
        pie.data = slices
        options.series = arrayListOf(pie)
        pieChart.options = options

        return pieChart
    }
}
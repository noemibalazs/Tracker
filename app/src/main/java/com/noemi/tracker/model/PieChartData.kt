package com.noemi.tracker.model

import com.noemi.tracker.utils.TOTAL_ANGLE

data class PieChartData(
    val charts: List<ChartData>
) {
    private val totalAmount = charts.sumOf { it.value.toDouble() }.toFloat()

    fun sweepAngle(index: Int, percentage: Float): Float {
        val gap = calculateGap(percentage)
        val totalWithGap = totalAmountWithGap(percentage)
        val gapAngle = gapAngle(percentage)
        val amount = charts[index].value
        return (((amount + gap) / totalWithGap) * TOTAL_ANGLE) - gapAngle
    }

    private fun calculateGap(percentage: Float): Float {
        if (charts.isEmpty()) return 0f
        return (totalAmount / charts.size) * percentage
    }

    private fun totalAmountWithGap(percentage: Float): Float = totalAmount + (charts.size * calculateGap(percentage))

    fun gapAngle(percentage: Float): Float = (calculateGap(percentage) / totalAmountWithGap(percentage)) * TOTAL_ANGLE

}
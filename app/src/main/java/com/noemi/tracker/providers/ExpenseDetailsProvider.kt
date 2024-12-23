package com.noemi.tracker.providers

import android.icu.util.Calendar
import com.noemi.tracker.model.Currencies
import com.noemi.tracker.model.Months
import com.noemi.tracker.model.ExpensesTypes

object ExpenseDetailsProvider {

    private val calendar = Calendar.getInstance()

    fun expensesTypes(): List<String> = ExpensesTypes.entries.map { it.name }

    fun getCurrencies(): List<String> = Currencies.entries.map { it.name }

    fun expensesMonths(): List<String> {
        val indexOfCurrentMonth = calendar.get(Calendar.MONTH)
        val months = Months.entries.map { it.name }
        val sorted = months.subList(indexOfCurrentMonth, months.size).toMutableList()
        sorted.addAll(months.subList(0, indexOfCurrentMonth))
        return sorted.toSet().toList()
    }

    fun expensesYears(): List<Int> {
        val year = calendar.get(Calendar.YEAR)
        return (year..(year + 5)).toList()
    }

    fun currentPeriod(): Pair<Int, String> {
        val indexOfCurrentMonth = calendar.get(Calendar.MONTH)
        val month = Months.entries[indexOfCurrentMonth].name
        val year = calendar.get(Calendar.YEAR)
        return year to month
    }
}
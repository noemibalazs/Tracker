package com.noemi.tracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noemi.tracker.model.ChartData
import com.noemi.tracker.model.Currencies
import com.noemi.tracker.model.Expense
import com.noemi.tracker.model.ExpensesTypes.Companion.getColor
import com.noemi.tracker.usecase.CurrentMonthExpensesUseCase
import com.noemi.tracker.usecase.SelectedPeriodExpensesUseCase
import com.noemi.tracker.providers.ExpenseDetailsProvider.currentPeriod
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesMonths
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesYears
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val currentMonthExpensesUseCase: CurrentMonthExpensesUseCase,
    private val selectedPeriodExpensesUseCase: SelectedPeriodExpensesUseCase
) : ViewModel() {

    private val expenseMonth = MutableSharedFlow<String>()
    private val expenseYear = MutableSharedFlow<Int>()

    private val _currentPeriodExpenses = MutableStateFlow(mapOf<String, List<Expense>>())
    val currentPeriodExpenses = _currentPeriodExpenses.asStateFlow()

    private val _currentPeriodDataCharts = MutableStateFlow(emptyList<ChartData>())
    val currentPeriodDataCharts = _currentPeriodDataCharts.asStateFlow()

    private val _selectedPeriodExpenses = MutableStateFlow(mapOf<String, List<Expense>>())
    val selectedPeriodExpenses = _selectedPeriodExpenses.asStateFlow()

    private val _selectedPeriodDataCharts = MutableStateFlow(emptyList<ChartData>())
    val selectedPeriodDataCharts = _selectedPeriodDataCharts.asStateFlow()

    private val _loadingCurrentPeriodExpenses = MutableStateFlow(false)
    val loadingCurrentPeriodExpenses = _loadingCurrentPeriodExpenses.asStateFlow()

    private val _loadingSelectedPeriodExpenses = MutableStateFlow(false)
    val loadingSelectedPeriodExpenses = _loadingSelectedPeriodExpenses.asStateFlow()

    private val _currency = MutableStateFlow("")
    val currency = _currency.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _isContinueEnabled = MutableStateFlow(false)
    val isContinueEnabled = _isContinueEnabled.asStateFlow()

    var monthExpanded by mutableStateOf(false)
        private set
    var monthIndex by mutableIntStateOf(0)
        private set
    var yearExpanded by mutableStateOf(false)
        private set
    var yearIndex by mutableIntStateOf(0)
        private set

    var selectedPeriod = Pair(0, "")
    val currentPeriod = currentPeriod()

    init {
        getCurrentExpenses()
        isContinueButtonEnabled()
    }

    private fun getCurrentExpenses() {
        viewModelScope.launch {

            _loadingCurrentPeriodExpenses.emit(true)
            delay(900)

            currentMonthExpensesUseCase.invoke()
                .catch {
                    _loadingCurrentPeriodExpenses.emit(false)
                    _errorMessage.emit(it.message ?: "Error while getting current expenses")
                }.collectLatest {

                    when (it.isEmpty()) {
                        true -> {
                            _currentPeriodExpenses.emit(emptyMap())
                            _currentPeriodDataCharts.emit(emptyList())
                            _currency.emit(getCurrency(emptyList()))
                            _errorMessage.emit("There are no recorded expenses for the current period.")
                        }

                        else -> {
                            val data = getChartsData(it)
                            _currentPeriodDataCharts.emit(data)
                            _currentPeriodExpenses.emit(it)
                            val expenses = it[it.keys.first()] ?: emptyList()
                            _currency.emit(getCurrency(expenses))
                        }
                    }
                    _loadingCurrentPeriodExpenses.emit(false)
                }
        }
    }

    private fun isContinueButtonEnabled() {
        viewModelScope.launch {
            combine(expenseMonth, expenseYear) { values ->
                values.map {
                    when (it) {
                        is String -> it.isNotEmpty()
                        is Int -> it > 0
                        else -> false
                    }
                }.all { it }
            }.collectLatest { result ->
                _isContinueEnabled.emit(result)
            }
        }
    }

    fun onMonthExpandedChanged(monthChanged: Boolean) {
        monthExpanded = monthChanged
    }

    fun onMonthIndexChanged(newIndex: Int, month: String) {
        monthIndex = newIndex
        viewModelScope.launch {
            expenseMonth.emit(month)
        }
        isContinueButtonEnabled()
    }

    fun onYearExpandedChanged(yearsChanged: Boolean) {
        yearExpanded = yearsChanged
    }

    fun onYearsIndexChanged(newIndex: Int, year: Int) {
        yearIndex = newIndex
        viewModelScope.launch {
            expenseYear.emit(year)
        }
        isContinueButtonEnabled()
    }

    fun getSelectedPeriodExpenses() {
        viewModelScope.launch {

            _loadingSelectedPeriodExpenses.emit(true)
            _selectedPeriodExpenses.emit(emptyMap())
            _selectedPeriodDataCharts.emit(emptyList())
            delay(900)

            selectedPeriodExpensesUseCase.invoke(expensesYears()[yearIndex], expensesMonths()[monthIndex])
                .catch {
                    _loadingSelectedPeriodExpenses.emit(false)
                    _errorMessage.emit(it.message ?: "Error while getting selected period expenses")
                }
                .collectLatest {

                    when (it.isEmpty()) {
                        true -> {
                            _selectedPeriodExpenses.emit(emptyMap())
                            _selectedPeriodDataCharts.emit(emptyList())
                            _errorMessage.emit("There are no recorded expenses for the selected period.")
                        }

                        else -> {
                            _selectedPeriodExpenses.emit(it)
                            val data = getChartsData(it)
                            _selectedPeriodDataCharts.emit(data)
                        }
                    }
                    _loadingSelectedPeriodExpenses.emit(false)

                    selectedPeriod = expensesYears()[yearIndex] to expensesMonths()[monthIndex]
                    onMonthIndexChanged(0, "")
                    onYearsIndexChanged(0, 0)
                }
        }
    }

    fun onErrorChanged() {
        viewModelScope.launch {
            _errorMessage.emit("")
        }
    }

    private fun getChartsData(data: Map<String, List<Expense>>): List<ChartData> {
        var totalSum = 0f
        data.values.forEach {
            totalSum += getTotalAmount(it)
        }
        val coEfficient = 360f / totalSum
        var currentAngle = 0f

        val list = mutableListOf<ChartData>()

        data.forEach { (type, expenses) ->
            val amount = getTotalAmount(expenses)
            val angle = amount * coEfficient
            val range = currentAngle..currentAngle + angle
            currentAngle += angle
            val chartData = ChartData(
                type = type,
                color = getColor(type),
                value = amount,
                range = range
            )
            list.add(chartData)
        }

        return list
    }

    private fun getTotalAmount(expense: List<Expense>): Float =
        expense.sumOf { it.amount }.toFloat()

    private fun getCurrency(expense: List<Expense>): String = when (expense.isEmpty()) {
        true -> Currencies.RON.name
        else -> expense[0].currency
    }
}
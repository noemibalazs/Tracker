package com.noemi.tracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noemi.tracker.model.Expense
import com.noemi.tracker.repository.ExpensesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesTypes
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesYears
import com.noemi.tracker.providers.ExpenseDetailsProvider.getCurrencies
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesMonths
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository
) : ViewModel() {

    private val expenseType = MutableStateFlow("")
    private val expenseMonth = MutableStateFlow("")
    private val expenseYear = MutableStateFlow(0)
    private val expenseCost = MutableStateFlow(0.0)
    private val docNumber = MutableStateFlow("")
    private val currency = MutableStateFlow("")

    private val _isSaveEnabled = MutableStateFlow(false)
    val isSaveEnabled = _isSaveEnabled.asStateFlow()

    init {
        isSaveButtonEnabled()
    }

    private fun isSaveButtonEnabled() {
        viewModelScope.launch {
            combine(expenseType, docNumber, expenseMonth, expenseYear, expenseCost, currency) { values ->
                values.map {
                    when (it) {
                        is String -> it.isNotEmpty()
                        is Int -> it > 0
                        is Double -> it > 0.0
                        else -> false
                    }
                }.all { it }
            }.collectLatest { result ->
                _isSaveEnabled.emit(result)
            }
        }
    }

    var typeExpanded by mutableStateOf(false)
        private set
    var typeIndex by mutableIntStateOf(0)
        private set
    var documentNumber by mutableStateOf("")
        private set
    var monthExpanded by mutableStateOf(false)
        private set
    var monthIndex by mutableIntStateOf(0)
        private set
    var yearExpanded by mutableStateOf(false)
        private set
    var yearIndex by mutableIntStateOf(0)
        private set
    var currencyExpanded by mutableStateOf(false)
        private set
    var currencyIndex by mutableIntStateOf(0)
        private set
    var expensesAmount by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)

    fun onTypeExpandedChanged(changed: Boolean) {
        typeExpanded = changed
    }

    fun onTypePositionChanged(position: Int, type: String) {
        typeIndex = position
        viewModelScope.launch {
            expenseType.emit(type)
        }
        isSaveButtonEnabled()
    }

    fun onDocumentNumberChanged(number: String) {
        documentNumber = number
        viewModelScope.launch {
            docNumber.emit(number)
        }
        isSaveButtonEnabled()
    }

    fun onMonthExpandedChanged(monthChanged: Boolean) {
        monthExpanded = monthChanged
    }

    fun onMonthIndexChanged(newIndex: Int, month: String) {
        monthIndex = newIndex
        viewModelScope.launch {
            expenseMonth.emit(month)
        }
        isSaveButtonEnabled()
    }

    fun onYearExpandedChanged(yearsChanged: Boolean) {
        yearExpanded = yearsChanged
    }

    fun onYearsIndexChanged(newIndex: Int, year: Int) {
        yearIndex = newIndex
        viewModelScope.launch {
            expenseYear.emit(year)
        }
        isSaveButtonEnabled()
    }

    fun onCurrencyExpandedChanged(currencyChanged: Boolean) {
        currencyExpanded = currencyChanged
    }

    fun onCurrencyIndexChanged(newIndex: Int, symbol: String) {
        currencyIndex = newIndex
        viewModelScope.launch {
            currency.emit(symbol)
        }
        isSaveButtonEnabled()
    }

    fun onAmountChanged(amount: String) {
        if (amount.isNotEmpty()) expensesAmount = amount
        viewModelScope.launch {
            if (amount.isNotEmpty()) expenseCost.emit(amount.toDouble())
        }
        isSaveButtonEnabled()
    }

    fun saveExpenses() {
        isLoading = true

        viewModelScope.launch {
            val expense = Expense(
                type = expensesTypes()[typeIndex],
                documentNumber = documentNumber,
                currency = getCurrencies()[currencyIndex],
                month = expensesMonths()[monthIndex],
                year = expensesYears()[yearIndex],
                amount = expensesAmount.toDouble()
            )

            expensesRepository.insertExpense(expense)

            delay(900)
            clearFields()
        }
    }

    private fun clearFields() {
        isLoading = false

        onTypePositionChanged(0, "")
        onMonthIndexChanged(0, "")
        onYearsIndexChanged(0, 0)
        onCurrencyIndexChanged(0, "")
        onDocumentNumberChanged("")
        expensesAmount = ""

        isSaveButtonEnabled()
    }
}
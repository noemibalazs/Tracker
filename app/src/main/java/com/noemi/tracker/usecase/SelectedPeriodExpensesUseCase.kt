package com.noemi.tracker.usecase

import com.noemi.tracker.model.Expense
import com.noemi.tracker.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class SelectedPeriodExpensesUseCase @Inject constructor(
    private val expensesRepository: ExpensesRepository
) {

    operator fun invoke(year: Int, month: String): Flow<Map<String, List<Expense>>> =
        expensesRepository.getExpenses(year, month)
            .map { expenses ->
                expenses.groupBy { it.type }
                    .mapValues { values -> values.value.map { it }.sortedBy { it.id } }
            }
}
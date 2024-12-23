package com.noemi.tracker.usecase

import com.noemi.tracker.model.Expense
import com.noemi.tracker.repository.ExpensesRepository
import com.noemi.tracker.providers.ExpenseDetailsProvider.currentPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CurrentMonthExpensesUseCase @Inject constructor(
    private val expensesRepository: ExpensesRepository
) {

    operator fun invoke(): Flow<Map<String, List<Expense>>> {
        val currentPeriod = currentPeriod()
        val year = currentPeriod.first
        val month = currentPeriod.second

        return expensesRepository.getExpenses(year, month)
            .map { expenses ->
                expenses.groupBy { it.type }
                    .mapValues { values -> values.value.map { it }.sortedBy { it.id } }
            }
    }
}
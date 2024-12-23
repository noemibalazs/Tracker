package com.noemi.tracker.repository

import com.noemi.tracker.model.Expense
import com.noemi.tracker.providers.DispatcherProvider
import com.noemi.tracker.room.ExpenseDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val expenseDAO: ExpenseDAO,
    private val dispatcherProvider: DispatcherProvider
) : ExpensesRepository {

    override suspend fun insertExpense(expense: Expense) = withContext(dispatcherProvider.io()) {
        expenseDAO.insert(expense)
    }

    override suspend fun deleteExpense(expense: Expense) = withContext(dispatcherProvider.io()) {
        expenseDAO.deleteExpense(expense)
    }

    override fun getExpenses(year: Int, month: String): Flow<List<Expense>> =
        expenseDAO.getExpenses(year, month)
            .map { it ?: emptyList() }
            .flowOn(dispatcherProvider.io())
}
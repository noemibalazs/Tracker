package com.noemi.tracker.repository

import com.noemi.tracker.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {

    suspend fun insertExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)

    fun getExpenses(year: Int, month: String): Flow<List<Expense>>
}
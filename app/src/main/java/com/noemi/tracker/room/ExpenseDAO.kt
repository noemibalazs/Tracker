package com.noemi.tracker.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noemi.tracker.model.Expense
import com.noemi.tracker.utils.EXPENSE_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM $EXPENSE_TABLE WHERE year = :year AND month = :month")
    fun getExpenses(year: Int, month: String): Flow<List<Expense>?>
}
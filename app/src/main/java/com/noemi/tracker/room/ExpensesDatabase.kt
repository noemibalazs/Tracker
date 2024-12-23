package com.noemi.tracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noemi.tracker.model.Expense

@Database(entities = [Expense::class], version = 1)
abstract class ExpensesDatabase : RoomDatabase() {

    abstract fun getExpenseDAO():ExpenseDAO
}
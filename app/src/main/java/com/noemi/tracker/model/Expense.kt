package com.noemi.tracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noemi.tracker.utils.EXPENSE_TABLE

@Entity(tableName = EXPENSE_TABLE)
data class Expense(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val year: Int,
    val month: String,
    val type: String,
    val documentNumber: String,
    val amount: Double,
    val currency: String
)

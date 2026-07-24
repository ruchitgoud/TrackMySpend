package com.ruchitgoud.trackmyspend.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val amount: Double,
    val type: String, // "income" or "expense"
    val date: Long // Timestamp
)

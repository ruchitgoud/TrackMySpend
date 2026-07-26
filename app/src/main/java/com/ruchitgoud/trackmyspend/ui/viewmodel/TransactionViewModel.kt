package com.ruchitgoud.trackmyspend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ruchitgoud.trackmyspend.data.Transaction
import com.ruchitgoud.trackmyspend.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ViewMode { TOTAL, MONTHLY }

data class TransactionSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val overallBalance: Double = 0.0
)

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _viewMode = MutableStateFlow(ViewMode.TOTAL)
    val viewMode: StateFlow<ViewMode> = _viewMode

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<TransactionSummary> = combine(allTransactions, _viewMode) { transactions, mode ->
        calculateSummary(transactions, mode)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionSummary())

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.TOTAL) ViewMode.MONTHLY else ViewMode.TOTAL
    }

    fun addTransaction(description: String, amount: Double, type: String, date: Long) {
        viewModelScope.launch {
            repository.insert(Transaction(description = description, amount = amount, type = type, date = date))
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    fun importCsv(csvText: String) {
        viewModelScope.launch {
            val imported = com.ruchitgoud.trackmyspend.util.CsvUtils.parseCsv(csvText)
            imported.forEach { repository.insert(it) }
        }
    }

    fun getCsvData(): String {
        return com.ruchitgoud.trackmyspend.util.CsvUtils.generateCsv(allTransactions.value)
    }

    private fun calculateSummary(transactions: List<Transaction>, mode: ViewMode): TransactionSummary {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        var income = 0.0
        var expense = 0.0
        var totalNet = 0.0

        transactions.forEach { tx ->
            val txDate = Calendar.getInstance().apply { timeInMillis = tx.date }
            val isThisMonth = txDate.get(Calendar.MONTH) == currentMonth && txDate.get(Calendar.YEAR) == currentYear

            if (tx.type == "income") totalNet += tx.amount
            else totalNet -= tx.amount

            if (mode == ViewMode.TOTAL || isThisMonth) {
                if (tx.type == "income") income += tx.amount
                else expense += tx.amount
            }
        }
        return TransactionSummary(income, expense, income - expense, totalNet)
    }
}

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

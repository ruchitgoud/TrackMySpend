package com.ruchitgoud.trackmyspend.util

import com.ruchitgoud.trackmyspend.data.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    fun generateCsv(transactions: List<Transaction>): String {
        val sb = StringBuilder()
        sb.append("Date,Description,Type,Amount\n")
        transactions.forEach { tx ->
            val dateStr = dateFormat.format(Date(tx.date))
            val safeDesc = "\"${tx.description.replace("\"", "\"\"")}\""
            sb.append("$dateStr,$safeDesc,${tx.type},${tx.amount}\n")
        }
        return sb.toString()
    }

    fun parseCsv(csvText: String): List<Transaction> {
        val lines = csvText.split("\n")
        if (lines.isEmpty()) return emptyList()

        val transactions = mutableListOf<Transaction>()
        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue

            val cols = parseCsvLine(line)
            if (cols.size >= 4) {
                try {
                    val dateStr = cols[0]
                    val desc = cols[1]
                    val type = cols[2].lowercase()
                    val amount = cols[3].toDouble()
                    val date = dateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()

                    transactions.add(
                        Transaction(
                            description = desc,
                            amount = amount,
                            type = type,
                            date = date
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return transactions
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val char = line[i]
            if (char == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    current.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (char == ',' && !inQuotes) {
                result.add(current.toString())
                current = StringBuilder()
            } else {
                current.append(char)
            }
            i++
        }
        result.add(current.toString())
        return result
    }
}

package com.ruchitgoud.trackmyspend.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ruchitgoud.trackmyspend.data.Transaction
import com.ruchitgoud.trackmyspend.ui.components.*
import com.ruchitgoud.trackmyspend.ui.theme.*
import com.ruchitgoud.trackmyspend.ui.viewmodel.TransactionViewModel
import com.ruchitgoud.trackmyspend.ui.viewmodel.ViewMode
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackerScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val transactions by viewModel.allTransactions.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf<Transaction?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(it)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val csvText = reader.use { r -> r.readText() }
                viewModel.importCsv(csvText)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            try {
                val csvData = viewModel.getCsvData()
                context.contentResolver.openOutputStream(it)?.use { os ->
                    os.write(csvData.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TrackerHeader(
                onBack = onBack,
                onExport = {
                    saveLauncher.launch("TrackMySpend_Transactions.csv")
                },
                onShare = {
                    try {
                        val csvData = viewModel.getCsvData()
                        val fileName = "TrackMySpend_Transactions.csv"
                        val cacheFile = File(context.cacheDir, fileName)
                        val outputStream = FileOutputStream(cacheFile)
                        outputStream.use { it.write(csvData.toByteArray()) }

                        val contentUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            cacheFile
                        )

                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, contentUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Transactions")
                        context.startActivity(shareIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onImport = {
                    importLauncher.launch("text/*")
                }
            )
        },
        containerColor = BrutalistWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SummarySection(summary, viewMode, onToggleMode = { viewModel.toggleViewMode() })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AddTransactionForm(onAdd = { desc, amount, type, date ->
                viewModel.addTransaction(desc, amount, type, date)
            })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TransactionsList(
                transactions = transactions,
                onDelete = { showDeleteDialog = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDeleteDialog != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteTransaction(showDeleteDialog!!)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
    }
}

@Composable
fun TrackerHeader(onBack: () -> Unit, onExport: () -> Unit, onShare: () -> Unit, onImport: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BrutalistIconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrutalistBlack)
        }

        Text(
            text = "Track My Spend",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BrutalistBlack
        )

        Box {
            BrutalistIconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = BrutalistBlack)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(BrutalistWhite)
            ) {
                DropdownMenuItem(
                    text = { Text("Export to Storage", fontWeight = FontWeight.Bold) },
                    onClick = {
                        showMenu = false
                        onExport()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Share CSV", fontWeight = FontWeight.Bold) },
                    onClick = {
                        showMenu = false
                        onShare()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Import CSV", fontWeight = FontWeight.Bold) },
                    onClick = {
                        showMenu = false
                        onImport()
                    }
                )
            }
        }
    }
}

@Composable
fun SummarySection(summary: com.ruchitgoud.trackmyspend.ui.viewmodel.TransactionSummary, mode: ViewMode, onToggleMode: () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            BrutalistButton(
                text = "Showing: ${if (mode == ViewMode.TOTAL) "Total" else "Monthly"}",
                onClick = onToggleMode,
                shadowOffset = 2.dp,
                cornerRadius = 10.dp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        BrutalistCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SoftYellow
        ) {
            Column(modifier = Modifier.padding(20.dp, 24.dp)) {
                Text(
                    text = "Net ${if (mode == ViewMode.TOTAL) "Total" else "Monthly"} Balance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "₹${"%.2f".format(summary.netBalance)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryBox(
                label = "Total Income",
                amount = summary.totalIncome,
                color = Mint,
                modifier = Modifier.weight(1f)
            )
            SummaryBox(
                label = "Total Expenses",
                amount = summary.totalExpense,
                color = LightPink,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryBox(label: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    BrutalistCard(modifier = modifier, backgroundColor = color) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "₹${"%.2f".format(amount)}", fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun AddTransactionForm(onAdd: (String, Double, String, Long) -> Unit) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val context = LocalContext.current
    
    val dateSdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    Column {
        Text(text = "Add New", fontSize = 22.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker Field
        BrutalistCard(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = { datePickerDialog.show() },
            shadowOffset = 0.dp,
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateSdf.format(Date(selectedDate)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        BrutalistTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = "What was it for?",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BrutalistTextField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                placeholder = "₹ Amount",
                modifier = Modifier.weight(2f)
            )
            
            BrutalistCard(
                modifier = Modifier.weight(1f).height(56.dp),
                onClick = { type = if (type == "expense") "income" else "expense" },
                shadowOffset = 0.dp,
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = type.replaceFirstChar { it.uppercase() },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        BrutalistButton(
            text = "Add Transaction",
            onClick = {
                if (description.isNotBlank() && amount.isNotBlank()) {
                    onAdd(description, amount.toDouble(), type, selectedDate)
                    description = ""
                    amount = ""
                }
            },
            backgroundColor = Peach,
            shadowOffset = 6.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TransactionsList(transactions: List<Transaction>, onDelete: (Transaction) -> Unit) {
    Column {
        Text(text = "Transactions", fontSize = 22.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .border(3.dp, Color(0xFFCCCCCC), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No transactions yet!", fontWeight = FontWeight.SemiBold, color = BrutalistGray)
            }
        } else {
            transactions.forEach { tx ->
                TransactionCard(tx, onDelete)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction, onDelete: (Transaction) -> Unit) {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    
    BrutalistCard(
        modifier = Modifier.fillMaxWidth(),
        shadowOffset = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.description, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text(text = sdf.format(Date(transaction.date)), fontSize = 12.sp, color = BrutalistGray, fontWeight = FontWeight.Bold)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                val amountText = "${if (transaction.type == "income") "+" else "-"}₹${"%.2f".format(transaction.amount)}"
                Text(
                    text = amountText,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = if (transaction.type == "income") Color(0xFF059669) else Color(0xFFDC2626)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                BrutalistIconButton(
                    onClick = { onDelete(transaction) },
                    backgroundColor = BrutalistWhite,
                    shadowOffset = 2.dp,
                    cornerRadius = 8.dp
                ) {
                    Text(text = "✕", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            BrutalistButton(text = "Yes", onClick = onConfirm, backgroundColor = LightPink, shadowOffset = 3.dp, cornerRadius = 12.dp)
        },
        dismissButton = {
            BrutalistButton(text = "No", onClick = onDismiss, backgroundColor = BrutalistWhite, shadowOffset = 3.dp, cornerRadius = 12.dp)
        },
        title = { Text(text = "Delete Transaction?", fontWeight = FontWeight.Black) },
        text = { Text(text = "This action cannot be undone.", fontWeight = FontWeight.Bold) },
        containerColor = BrutalistWhite,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.border(3.dp, BrutalistBlack, RoundedCornerShape(24.dp))
    )
}

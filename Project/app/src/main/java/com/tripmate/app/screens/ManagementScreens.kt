package com.tripmate.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tripmate.app.data.MockDataProvider
import com.tripmate.app.models.Expense
import com.tripmate.app.models.Task
import com.tripmate.app.models.Event
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(navController: NavController, tripId: String?) {
    val expenses = MockDataProvider.expenses.filter { it.tripId == tripId }
    val total = remember(expenses) { expenses.sumOf { it.amount } }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Add Expense")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Total Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Spent", style = MaterialTheme.typography.labelMedium)
                    AnimatedCounter(total.toInt())
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Budget Meter (Innovation)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Budget usage", style = MaterialTheme.typography.labelSmall)
                            Text("75%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = 0.75f,
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp)
            ) {
                itemsIndexed(expenses) { index, expense ->
                    AnimatedListItem(index) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            ListItem(
                                headlineContent = { Text(expense.title, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text("Just now", style = MaterialTheme.typography.bodySmall) },
                                trailingContent = { Text("₹${expense.amount.toInt()}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        GenericAddDialog(
            title = "Add Expense",
            onDismiss = { showAddDialog = false },
            onSave = { title, amountStr ->
                MockDataProvider.addExpense(
                    Expense(
                        id = System.currentTimeMillis().toString(),
                        tripId = tripId ?: "",
                        title = title,
                        amount = amountStr.toDoubleOrNull() ?: 0.0,
                        timestamp = System.currentTimeMillis()
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(navController: NavController, tripId: String?) {
    val tasks = MockDataProvider.tasks.filter { it.tripId == tripId }
    var newTaskName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Quick Add
            OutlinedTextField(
                value = newTaskName,
                onValueChange = { newTaskName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                placeholder = { Text("Add new task...") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (newTaskName.isNotBlank()) {
                            MockDataProvider.addTask(Task(System.currentTimeMillis().toString(), tripId ?: "", newTaskName))
                            newTaskName = ""
                        }
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )

            // Smart Checklist Innovation
            TextButton(
                onClick = {
                    MockDataProvider.addTask(com.tripmate.app.models.Task(System.currentTimeMillis().toString(), tripId ?: "", "Packing: Clothes"))
                    MockDataProvider.addTask(com.tripmate.app.models.Task(System.currentTimeMillis().toString(), tripId ?: "", "Documents: Passport"))
                    MockDataProvider.addTask(com.tripmate.app.models.Task(System.currentTimeMillis().toString(), tripId ?: "", "Travel: Charger"))
                },
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text("Smart Checklist (Auto-gen)")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp)
            ) {
                items(tasks) { task ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = if (task.completed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    task.name,
                                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (task.completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = task.completed,
                                    onCheckedChange = { 
                                        MockDataProvider.updateTask(task.copy(completed = it))
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(navController: NavController, tripId: String?) {
    val events = MockDataProvider.events.filter { it.tripId == tripId }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerary") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.CalendarToday, "Add Event")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(24.dp)
        ) {
            itemsIndexed(events) { index, event ->
                AnimatedListItem(index) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HistoryEdu, null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(Modifier.width(4.dp))
                                    Text("${event.date} • ${event.time}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Icon(Icons.Default.DragHandle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        GenericAddDialog(
            title = "Add Event",
            onDismiss = { showAddDialog = false },
            onSave = { title, time ->
                MockDataProvider.addEvent(
                    Event(
                        id = System.currentTimeMillis().toString(),
                        tripId = tripId ?: "",
                        title = title,
                        date = "May 20, 2024",
                        time = time
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AnimatedCounter(target: Int) {
    var count by remember { mutableStateOf(0) }
    LaunchedEffect(target) {
        val step = (target / 10).coerceAtLeast(1)
        while (count < target) {
            delay(20)
            count = (count + step).coerceAtMost(target)
        }
    }
    Text(
        "₹$count",
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun AnimatedListItem(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 100L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn()
    ) {
        content()
    }
}

@Composable
fun GenericAddDialog(title: String, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Name/Title") }, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Amount/Time") }, shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(onClick = { onSave(val1, val2) }) { Text("Save") }
        }
    )
}

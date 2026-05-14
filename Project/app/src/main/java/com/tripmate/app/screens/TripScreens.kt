package com.tripmate.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tripmate.app.data.MockDataProvider
import com.tripmate.app.models.Expense
import com.tripmate.app.models.Task
import com.tripmate.app.models.Event
import com.tripmate.app.navigation.Screen
import com.tripmate.app.ui.theme.Orange500

import com.tripmate.app.ui.components.HeaderIconButton

@Composable
fun TripDetailScreen(navController: NavController, tripId: String) {
    val trip = MockDataProvider.trips.find { it.id == tripId } ?: return
    val tasks = MockDataProvider.tasks.filter { it.tripId == tripId }
    val completedCount = tasks.count { it.completed }
    val progress = if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size else 0f
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(Icons.Default.ArrowBack) { navController.popBackStack() }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Orange500.copy(alpha = 0.1f))
                    .clickable { navController.navigate(Screen.Collaboration.route) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = Orange500, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("GROUP", style = MaterialTheme.typography.labelSmall, color = Orange500, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Trip Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .clip(RoundedCornerShape(36.dp))
        ) {
            if (trip.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = trip.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1D1D1D)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(40.dp), tint = Color.White.copy(alpha = 0.2f))
                }
            }
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Info Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF1D1D1D))
                .padding(24.dp)
        ) {
            Column {
                Text(trip.destination, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, null, tint = Orange500, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(trip.date.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color(0xFFA1A1AA))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress & Health Score (Innovation)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Trip Completion", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = 0.65f,
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("65%", fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(0.6f),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Health Score", style = MaterialTheme.typography.labelSmall)
                            Text("92", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = Color(0xFF27272A), thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("TOTAL BUDGET", style = MaterialTheme.typography.labelSmall, color = Color(0xFF71717A))
                        Text("₹${trip.budget.toInt()}", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF27272A)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, null, tint = Orange500)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("ADVENTURE TOOLBOX", style = MaterialTheme.typography.labelSmall, color = Color(0xFF52525B))
        Spacer(modifier = Modifier.height(16.dp))

        ToolboxButton("Expenses", "Smart budget tracking", Icons.Default.CreditCard, Color(0xFF22C55E)) { navController.navigate(Screen.Expense.createRoute(tripId)) }
        Spacer(modifier = Modifier.height(16.dp))
        ToolboxButton("Checklist", "Preparation & packing", Icons.Default.CheckBox, Color(0xFF3B82F6)) { navController.navigate(Screen.Checklist.createRoute(tripId)) }
        Spacer(modifier = Modifier.height(16.dp))
        ToolboxButton("Itinerary", "Event & activity schedule", Icons.Default.Schedule, Color(0xFFA855F7)) { navController.navigate(Screen.Event.createRoute(tripId)) }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ToolboxButton(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF1D1D1D))
            .clickable { onClick() }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.width(20.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF71717A))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(navController: NavController, tripId: String) {
    val expenses = MockDataProvider.expenses.filter { it.tripId == tripId }
    val total = expenses.sumOf { it.amount }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = Orange500, contentColor = Color.Black) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(Icons.Default.ArrowBack) { navController.popBackStack() }
                Text("EXPENSES", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(48.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF1D1D1D))
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TOTAL EXPENSES", style = MaterialTheme.typography.labelSmall, color = Color(0xFF71717A), letterSpacing = 2.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("₹${total.toInt()}", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().alpha(0.3f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(48.dp), tint = Color(0xFF71717A))
                        Spacer(Modifier.height(16.dp))
                        Text("No expenses recorded yet.", color = Color(0xFF71717A))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(expenses) { expense ->
                        ExpenseItem(expense)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddExpenseDialog(
            onDismiss = { showDialog = false },
            onSave = { title, amount ->
                MockDataProvider.addExpense(
                    Expense(
                        id = System.currentTimeMillis().toString(),
                        tripId = tripId,
                        title = title,
                        amount = amount,
                        timestamp = System.currentTimeMillis()
                    )
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF1D1D1D))
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF27272A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CreditCard, null, tint = Color(0xFF22C55E), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(expense.title, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("EXPENSE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF52525B))
            }
        }
        Text("₹${expense.amount.toInt()}", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(navController: NavController, tripId: String) {
    val tasks = MockDataProvider.tasks.filter { it.tripId == tripId }
    val completedCount = tasks.count { it.completed }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = Orange500, contentColor = Color.Black) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(Icons.Default.ArrowBack) { navController.popBackStack() }
                Text("CHECKLIST", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(48.dp))
            }

            Column {
                Text("TASKS", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text("$completedCount OF ${tasks.size} COMPLETED", style = MaterialTheme.typography.labelSmall, color = Color(0xFF71717A))
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(tasks) { task ->
                    ChecklistItem(task) { MockDataProvider.tasks.find { it.id == task.id }?.let { it.completed = !it.completed } }
                }
            }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onSave = { taskName ->
                MockDataProvider.addTask(
                    Task(
                        id = System.currentTimeMillis().toString(),
                        tripId = tripId,
                        name = taskName
                    )
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun ChecklistItem(task: Task, onToggle: () -> Unit) {
    var checked by remember { mutableStateOf(task.completed) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF1D1D1D))
            .clickable { 
                checked = !checked
                onToggle()
            }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (checked) Orange500 else Color(0xFF27272A))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (checked) Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(20.dp))
        Text(
            task.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (checked) Color(0xFF52525B) else Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            textDecoration = if (checked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(navController: NavController, tripId: String) {
    val trip = MockDataProvider.trips.find { it.id == tripId }
    val events = MockDataProvider.events.filter { it.tripId == tripId }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = Orange500, contentColor = Color.Black) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(Icons.Default.ArrowBack) { navController.popBackStack() }
                Text("ITINERARY", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(48.dp))
            }

            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFF1D1D1D))
            ) {
                if (trip?.imageUrl?.isNotEmpty() == true) {
                    AsyncImage(model = trip.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(32.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(Orange500))
                        Spacer(Modifier.width(8.dp))
                        Text("LIVE DESTINATION VIEW", style = MaterialTheme.typography.labelSmall, color = Orange500)
                    }
                    Text(trip?.destination ?: "", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(events) { event ->
                    EventItem(event)
                }
            }
        }
    }

    if (showDialog) {
        AddEventDialog(
            onDismiss = { showDialog = false },
            onSave = { title, date, time ->
                MockDataProvider.addEvent(
                    Event(
                        id = System.currentTimeMillis().toString(),
                        tripId = tripId,
                        title = title,
                        date = date,
                        time = time
                    )
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun EventItem(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF1D1D1D))
            .padding(24.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF2B2B2B)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val day = event.date.split("-").lastOrNull() ?: ""
            val month = "MAY" // Simplified
            Text(day, style = MaterialTheme.typography.headlineMedium, color = Orange500, fontWeight = FontWeight.Bold)
            Text(month, style = MaterialTheme.typography.labelSmall, color = Color(0xFF71717A))
        }
        Spacer(Modifier.width(24.dp))
        Column {
            Text(event.title, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Schedule, null, tint = Orange500, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(8.dp))
                Text(event.time.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color(0xFF71717A))
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                error?.let { Text(it, color = Color(0xFFFF6B6B)) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull()
                when {
                    title.isBlank() -> error = "Title is required"
                    amount == null || amount <= 0.0 -> error = "Enter a valid amount"
                    else -> onSave(title.trim(), amount)
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddTaskDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = taskName, onValueChange = { taskName = it }, label = { Text("Task") }, singleLine = true)
                error?.let { Text(it, color = Color(0xFFFF6B6B)) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (taskName.isBlank()) {
                    error = "Task name is required"
                } else {
                    onSave(taskName.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddEventDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true)
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, singleLine = true)
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, singleLine = true)
                error?.let { Text(it, color = Color(0xFFFF6B6B)) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    title.isBlank() -> error = "Title is required"
                    date.isBlank() -> error = "Date is required"
                    time.isBlank() -> error = "Time is required"
                    else -> onSave(title.trim(), date.trim(), time.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

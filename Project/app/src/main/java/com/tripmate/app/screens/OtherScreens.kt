package com.tripmate.app.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tripmate.app.data.MockDataProvider
import com.tripmate.app.models.Member
import com.tripmate.app.navigation.Screen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborationScreen(navController: NavController, tripId: String) {
    val context = LocalContext.current
    val trip = MockDataProvider.trips.find { it.id == tripId } ?: return
    val members = MockDataProvider.members.filter { it.tripId == tripId }
    val notifications = MockDataProvider.notifications.filter { it.tripId == tripId || it.tripId == "" }.take(10)
    
    val tasks = MockDataProvider.tasks.filter { it.tripId == tripId }
    val expenses = MockDataProvider.expenses.filter { it.tripId == tripId }
    val events = MockDataProvider.events.filter { it.tripId == tripId }
    
    val completionPercentage = if (tasks.isEmpty()) 0f else (tasks.count { it.completed }.toFloat() / tasks.size.toFloat())
    val totalSpent = expenses.sumOf { it.amount }
    
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collaboration", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Refresh logic */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Trip Header Section
            TripHeaderSection(trip.destination, trip.inviteCode.ifEmpty { "TRIP-${trip.id.takeLast(4)}" }, members.size) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Trip Code", it)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Code Copied!", Toast.LENGTH_SHORT).show()
            }

            // 6. Shared Progress Section (Moved up for visibility)
            SharedProgressSection(completionPercentage, totalSpent, trip.budget, events.size)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Invite / Join Section
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showAddMemberDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.PersonAdd, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Invite")
                }
                OutlinedButton(
                    onClick = { showJoinDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.GroupAdd, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Join Trip")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Quick Actions Panel
            SectionHeader("Quick Planning")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { QuickActionCard("Add Expense", Icons.Default.AddCard) { navController.navigate(Screen.Expense.createRoute(tripId)) } }
                item { QuickActionCard("Checklist", Icons.Default.PlaylistAddCheck) { navController.navigate(Screen.Checklist.createRoute(tripId)) } }
                item { QuickActionCard("Add Event", Icons.Default.CalendarToday) { navController.navigate(Screen.Event.createRoute(tripId)) } }
                item { QuickActionCard("Trip Details", Icons.Default.Info) { navController.navigate(Screen.TripDetail.createRoute(tripId)) } }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Members List Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader("Members", modifier = Modifier.padding(horizontal = 0.dp))
                Text("${members.size} Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                members.forEachIndexed { index, member ->
                    MemberItem(member, index)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Activity Feed (Timeline)
            SectionHeader("Live Activity Feed")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (notifications.isEmpty()) {
                        Text("No recent activity.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    } else {
                        notifications.forEach { notif ->
                            ActivityFeedItem(notif)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            
            // 7. Leave / Remove Options
            TextButton(
                onClick = { /* Leave Trip logic */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(Modifier.width(8.dp))
                Text("Leave Trip Session")
            }
            
            Spacer(modifier = Modifier.height(64.dp))
        }
    }

    if (showAddMemberDialog) {
        GenericAddDialog(
            title = "Invite Member",
            onDismiss = { showAddMemberDialog = false },
            onSave = { name, email ->
                MockDataProvider.addMember(Member(System.currentTimeMillis().toString(), tripId, name, email))
                showAddMemberDialog = false
            }
        )
    }
    
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Join Trip") },
            text = {
                var code by remember { mutableStateOf("") }
                Column {
                    Text("Enter the 8-digit invite code.")
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Invite Code") })
                }
            },
            confirmButton = {
                Button(onClick = { showJoinDialog = false }) { Text("Join") }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun TripHeaderSection(name: String, code: String, memberCount: Int, onCopyCode: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FlightTakeoff, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("$memberCount Members Planning", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text("Invite Code", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .clickable { onCopyCode(code) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(code, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Icon(Icons.Default.ContentCopy, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SharedProgressSection(completion: Float, spent: Double, budget: Double, events: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProgressCard("Planning", "${(completion * 100).toInt()}%", completion, Modifier.weight(1f))
        ProgressCard("Budget", "₹${(spent/1000).toInt()}k", (spent/budget).toFloat().coerceIn(0f, 1f), Modifier.weight(1f))
        ProgressCard("Events", "$events", 1f, Modifier.weight(1f))
    }
}

@Composable
fun ProgressCard(label: String, value: String, progress: Float, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun MemberItem(member: Member, index: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent
        ) {
            ListItem(
                headlineContent = { Text(member.name, fontWeight = FontWeight.Bold) },
                supportingContent = { Text(member.email.ifEmpty { "member@tripmate.com" }, style = MaterialTheme.typography.bodySmall) },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(member.name.first().toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                },
                trailingContent = {
                    Surface(
                        color = if (member.role == "Owner") MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            member.role,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (member.role == "Owner") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ActivityFeedItem(notif: com.tripmate.app.models.Notification) {
    Row(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when(notif.type) {
                    "expense" -> Icons.Default.AttachMoney
                    "task" -> Icons.Default.CheckCircle
                    "event" -> Icons.Default.Event
                    else -> Icons.Default.History
                },
                null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(notif.message, style = MaterialTheme.typography.bodyMedium)
            Text("Just now", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = MockDataProvider.notifications

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(24.dp)
        ) {
            itemsIndexed(notifications) { index, notif ->
                AnimatedListItem(index) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        ListItem(
                            headlineContent = { Text(notif.message) },
                            supportingContent = { Text("Just now", style = MaterialTheme.typography.labelSmall) },
                            leadingContent = {
                                Icon(
                                    when (notif.type) {
                                        "trip" -> Icons.Default.Flight
                                        "expense" -> Icons.Default.AccountBalanceWallet
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddMemoryDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    
    val user = MockDataProvider.currentUser
    val memories = MockDataProvider.travelMemories
    
    val scaleAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scaleAnim.value)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clickable { showEditDialog = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.padding(8.dp).size(16.dp), tint = Color.Black)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(user.status, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showEditDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Text("Edit Profile")
            }

            Spacer(Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatCard("Trips", user.tripsCount.toString()) { 
                    // open trips details mock
                    showAddMemoryDialog = false
                }
                ProfileStatCard("Countries", user.countriesCount.toString()) { 
                    showAddMemoryDialog = false
                }
                ProfileStatCard("Budget", user.budgetSpent) { 
                    showAddMemoryDialog = false
                }
            }

            Spacer(Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader("TRAVEL MEMORY TIMELINE", modifier = Modifier.padding(horizontal = 0.dp))
                TextButton(onClick = { showAddMemoryDialog = true }) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Text("Add Memory", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.height(16.dp))
            if (memories.isEmpty()) {
                Text("No memories yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    memories.forEach { memory ->
                        MemoryItem(memory.city, memory.date, memory.comment)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text("ACCOUNT SETTINGS", modifier = Modifier.align(Alignment.Start), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(8.dp))
            ProfileMenu(title = "Security Settings", icon = Icons.Default.Shield) { showSecurityDialog = true }
            ProfileMenu(title = "Payment Methods", icon = Icons.Default.Payment) { showPaymentDialog = true }
            
            Spacer(Modifier.height(16.dp))
            Text("APP PREFERENCES", modifier = Modifier.align(Alignment.Start), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(8.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Switch(checked = MockDataProvider.isDarkMode, onCheckedChange = { MockDataProvider.isDarkMode = it })
                }
            }

            ProfileMenu(title = "Cloud Tasks", icon = Icons.Default.Cloud) { 
                navController.navigate(Screen.Todo.route)
            }
            
            Spacer(Modifier.height(32.dp))
            
            TextButton(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Logout Session")
            }
            
            Spacer(Modifier.height(48.dp))
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                MockDataProvider.updateProfile(updatedUser)
                showEditDialog = false
            }
        )
    }

    if (showAddMemoryDialog) {
        AddMemoryDialog(
            onDismiss = { showAddMemoryDialog = false },
            onSave = { city, date, comment ->
                MockDataProvider.addMemory(com.tripmate.app.models.TravelMemory(System.currentTimeMillis().toString(), city, date, comment))
                showAddMemoryDialog = false
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSecurityDialog) {
        SecuritySettingsDialog(onDismiss = { showSecurityDialog = false })
    }

    if (showPaymentDialog) {
        PaymentMethodsDialog(onDismiss = { showPaymentDialog = false })
    }
}

@Composable
fun EditProfileDialog(user: com.tripmate.app.models.UserProfile, onDismiss: () -> Unit, onSave: (com.tripmate.app.models.UserProfile) -> Unit) {
    var name by remember { mutableStateOf(user.name) }
    var status by remember { mutableStateOf(user.status) }
    var email by remember { mutableStateOf(user.email) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            }
        },
        confirmButton = { Button(onClick = { onSave(user.copy(name = name, status = status, email = email)) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddMemoryDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var city by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Travel Memory") },
        text = {
            Column {
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City/Location") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Month & Year") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = comment, onValueChange = { comment = it }, label = { Text("Your Memory") })
            }
        },
        confirmButton = { Button(onClick = { onSave(city, date, comment) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ProfileMenu(title: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun ProfileStatCard(label: String, value: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
    }
}


@Composable
fun SecuritySettingsDialog(onDismiss: () -> Unit) {
    var changePwd by remember { mutableStateOf("") }
    var twoFa by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Security Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Two-Factor Authentication")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enabled", modifier = Modifier.weight(1f))
                    Switch(checked = twoFa, onCheckedChange = { twoFa = it })
                }
                Spacer(Modifier.height(8.dp))
                Text("Active Sessions")
                Text("• Pixel 5 - Active")
                Text("• Chrome on Desktop - Active")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = changePwd, onValueChange = { changePwd = it }, label = { Text("New Password") })
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun PaymentMethodsDialog(onDismiss: () -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Payment Methods") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Saved Cards")
                Text("• Visa **** 4242")
                Text("• Amex **** 0005")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = cardNumber, onValueChange = { cardNumber = it }, label = { Text("Add Card Number") })
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun MemoryItem(city: String, date: String, comment: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        Box(modifier = Modifier.width(2.dp).height(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(city, fontWeight = FontWeight.Bold)
            Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(comment, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavController) {
    val trips = MockDataProvider.trips
    var showJoinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Hub", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = { showJoinDialog = true }) {
                        Icon(Icons.Default.GroupAdd, contentDescription = "Join Trip")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Collaborate & Explore", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(8.dp))
                    Text("Plan trips together with friends and family in real-time.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showJoinDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Join via Code")
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            SectionHeader("Active Collaborations", modifier = Modifier.padding(horizontal = 0.dp))

            if (trips.isEmpty()) {
                Text(
                    "You don't have any active trips.",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    trips.forEachIndexed { index, trip ->
                        AnimatedListItem(index) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { navController.navigate(Screen.Collaboration.createRoute(trip.id)) },
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        // A small placeholder or the actual image could go here, for now an icon
                                        Icon(Icons.Default.FlightTakeoff, null, modifier = Modifier.align(Alignment.Center), tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(trip.destination, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("${MockDataProvider.members.filter { it.tripId == trip.id }.size} Members • ${trip.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { /* open collaboration */ navController.navigate(Screen.Collaboration.createRoute(trip.id)) }) {
                                            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { 
                                            // open invite dialog for this trip
                                            // set states below via mutableState
                                        }) {
                                            Icon(Icons.Default.PersonAdd, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(100.dp))
        }
    }

    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Join Collaboration") },
            text = {
                var code by remember { mutableStateOf("") }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the 8-digit invite code shared by your friend.")
                    OutlinedTextField(
                        value = code, 
                        onValueChange = { if (it.length <= 8) code = it.uppercase() }, 
                        label = { Text("Invite Code") }, 
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("TRIP-XXXX") }
                    )
                }
            },
            confirmButton = { 
                Button(onClick = { showJoinDialog = false }) { Text("Join Trip") } 
            },
            dismissButton = { TextButton(onClick = { showJoinDialog = false }) { Text("Cancel") } }
        )
    }

    // Invite dialog state
    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteTripId by remember { mutableStateOf("") }

    // We need to trigger invite for a specific trip when pressing PersonAdd; replace the placeholder above with this local logic.
    // For simplicity, add a floating invite button that targets the first trip if none selected.
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                inviteTripId = trips.firstOrNull()?.id ?: ""
                showInviteDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.PersonAdd, null, tint = Color.Black)
        }
    }

    if (showInviteDialog) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("Member") }

        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Invite Friend") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email (optional)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            // Add two demo users quickly
                            val tId = inviteTripId.ifEmpty { trips.firstOrNull()?.id ?: "" }
                            val m1 = Member(System.currentTimeMillis().toString(), tId, "Alice Demo", "alice@example.com")
                            val m2 = Member((System.currentTimeMillis()+1).toString(), tId, "Bob Demo", "bob@example.com")
                            MockDataProvider.addMember(m1)
                            MockDataProvider.addMember(m2)
                            showInviteDialog = false
                        }) { Text("Accept Invite (Demo)") }
                        Spacer(Modifier.width(8.dp))
                        Text("Demo Invite Code: TRIP-${(1000..9999).random()}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val id = System.currentTimeMillis().toString()
                    val member = Member(id, inviteTripId.ifEmpty { trips.firstOrNull()?.id ?: "" }, name.ifEmpty { "Guest" }, email)
                    MockDataProvider.addMember(member)
                    showInviteDialog = false
                }, enabled = name.isNotEmpty()) { Text("Invite") }
            },
            dismissButton = {
                TextButton(onClick = { showInviteDialog = false }) { Text("Cancel") }
            },
            tonalElevation = 8.dp
        )
    }
}

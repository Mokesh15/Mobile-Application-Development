package com.tripmate.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tripmate.app.data.MockDataProvider
import com.tripmate.app.models.Trip
import com.tripmate.app.navigation.Screen
import com.tripmate.app.ui.components.HeaderIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val trips = MockDataProvider.trips
    val completedTrips = MockDataProvider.completedTrips
    val notifications = MockDataProvider.notifications
    val user = MockDataProvider.currentUser
    
    var showSmartPlanner by remember { mutableStateOf(false) }
    var showJoinTripDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var lastOpenedTripId by remember { mutableStateOf<String?>(trips.firstOrNull()?.id) }

    val filteredTrips = if (searchQuery.isEmpty()) trips else trips.filter { 
        it.destination.contains(searchQuery, ignoreCase = true) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Updated Header with Search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 48.dp, 24.dp, 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Where to next?", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                    Text("Hello, ${user.name.split(" ").first()}! Ready for adventure?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
                HeaderIconButton(icon = Icons.Default.Notifications, onClick = {
                    navController.navigate(Screen.Notifications.route)
                })
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search your adventures...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // Trip Summary Banner (Premium)
        TripSummaryBanner(completedTrips.size, user.budgetSpent)

        Spacer(Modifier.height(32.dp))

        // Quick Actions Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionChip(Icons.Default.Add, "Create Trip", Modifier.weight(1f)) { 
                // Navigate to Create Trip (currently logic inside Smart Planner)
                showSmartPlanner = true
            }
            ActionChip(Icons.Default.GroupAdd, "Join Trip", Modifier.weight(1f)) { 
                showJoinTripDialog = true
            }
        }
        
        Spacer(Modifier.height(32.dp))

        // Upcoming Trips
        SectionHeader("Upcoming Adventures")
        if (filteredTrips.isEmpty()) {
            Text("No trips found matching '$searchQuery'", modifier = Modifier.padding(24.dp), color = MaterialTheme.colorScheme.secondary)
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().height(220.dp)
            ) {
                itemsIndexed(filteredTrips) { index, trip ->
                    CompactTripCard(trip) {
                        lastOpenedTripId = trip.id
                        navController.navigate(Screen.TripDetail.createRoute(trip.id))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Completed Trips
        SectionHeader("Completed Trips")
        if (completedTrips.isEmpty()) {
            Text("No completed trips yet", modifier = Modifier.padding(24.dp), color = MaterialTheme.colorScheme.secondary)
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().height(220.dp)
            ) {
                itemsIndexed(completedTrips) { _, trip ->
                    CompactTripCard(trip) {
                        lastOpenedTripId = trip.id
                        navController.navigate(Screen.TripDetail.createRoute(trip.id))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Continue Planning (Innovation)
        lastOpenedTripId?.let { id ->
            trips.find { it.id == id }?.let { trip ->
                SectionHeader("Continue Planning")
                ContinuePlanningCard(trip) {
                    navController.navigate(Screen.TripDetail.createRoute(trip.id))
                }
            }
        }

        // Travel Insights & Tips (Premium Touch)
        Spacer(modifier = Modifier.height(32.dp))
        SectionHeader("Travel Insights")
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Pro Tip", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Pack a portable charger for your upcoming Bali trip to stay powered during treks.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Activity
        SectionHeader("Recent Activity")
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            notifications.take(3).forEach { notif ->
                ActivityRow(notif)
            }
        }
    }

    if (showSmartPlanner) {
        SmartPlannerDialog(onDismiss = { showSmartPlanner = false }) { dest, days, budget ->
            val tripId = System.currentTimeMillis().toString()
            val newTrip = Trip(
                id = tripId,
                destination = dest,
                date = "July 2024",
                budget = budget,
                imageUrl = "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?auto=format&fit=crop&w=800&q=80"
            )
            MockDataProvider.addTrip(newTrip)
            showSmartPlanner = false
            navController.navigate(Screen.TripDetail.createRoute(tripId))
        }
    }

    if (showJoinTripDialog) {
        JoinTripDialog(onDismiss = { showJoinTripDialog = false }) { code ->
            // Logic to join trip via code
            MockDataProvider.addNotification("Joined trip via code: $code", "trip")
            showJoinTripDialog = false
        }
    }
}

@Composable
fun ActionChip(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun CompactTripCard(trip: Trip, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Box {
            AsyncImage(model = trip.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(trip.destination, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(trip.date, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ActivityRow(notif: com.tripmate.app.models.Notification) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primary))
        Spacer(Modifier.width(16.dp))
        Text(notif.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ContinuePlanningCard(trip: Trip, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))) {
                AsyncImage(model = trip.imageUrl, contentDescription = null, contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(trip.destination, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Continue where you left off", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Icon(Icons.Default.ChevronRight, null)
        }
    }
}

@Composable
fun SmartPlannerDialog(onDismiss: () -> Unit, onGenerate: (String, Int, Double) -> Unit) {
    var dest by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("3") }
    var budget by remember { mutableStateOf("50000") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("AI Smart Planner")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Let Tripmate AI design your perfect itinerary based on your preferences.")
                OutlinedTextField(value = dest, onValueChange = { dest = it }, label = { Text("Destination City") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = days, onValueChange = { days = it }, label = { Text("Duration (Days)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = budget, onValueChange = { budget = it }, label = { Text("Target Budget (₹)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { 
            Button(onClick = { onGenerate(dest, days.toIntOrNull() ?: 1, budget.toDoubleOrNull() ?: 0.0) }) { 
                Text("Generate Plan") 
            } 
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun JoinTripDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var code by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join Collaboration") },
        text = {
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
        confirmButton = { Button(onClick = { onJoin(code) }, enabled = code.length >= 4) { Text("Join Trip") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun TripSummaryBanner(tripsCount: Int, budgetSpent: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Travel Summary", color = Color.Black.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                Text("$tripsCount Completed Trips", color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Total Savings", color = Color.Black.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                Text("₹24.5k", color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
        }
    }
}

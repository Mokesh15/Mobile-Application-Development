package com.tripmate.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tripmate.app.data.MockDataProvider
import com.tripmate.app.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(navController: NavController, tripId: String?) {
    val trip = MockDataProvider.trips.find { it.id == tripId } ?: return
    val scrollState = rememberScrollState()

    LaunchedEffect(tripId) {
        tripId?.let { MockDataProvider.loadTripDetails(it) }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = trip.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        trip.destination,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(trip.date, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Budget Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Budget", style = MaterialTheme.typography.labelMedium)
                            Text("₹${trip.budget.toInt()}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        }
                        Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Group Collaboration Card (Innovation)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.Collaboration.createRoute(trip.id)) },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Groups, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Group Collaboration", fontWeight = FontWeight.Bold)
                            Text("Plan together with friends", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("QUICK ACCESS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCard("Expenses", Icons.Default.Receipt, Modifier.weight(1f)) {
                        navController.navigate(Screen.Expense.createRoute(trip.id))
                    }
                    FeatureCard("Checklist", Icons.Default.PlaylistAddCheck, Modifier.weight(1f)) {
                        navController.navigate(Screen.Checklist.createRoute(trip.id))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                FeatureCard("View Itinerary", Icons.Default.Map, Modifier.fillMaxWidth()) {
                    navController.navigate(Screen.Event.createRoute(trip.id))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("ITINERARY OVERVIEW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                val events = MockDataProvider.events.filter { it.tripId == tripId }
                if (events.isEmpty()) {
                    Text("No events planned yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                } else {
                    events.forEachIndexed { index, event ->
                        ItineraryItem(event, index == events.size - 1)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Custom Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}

@Composable
fun FeatureCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ItineraryItem(event: com.tripmate.app.models.Event, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        Spacer(Modifier.width(24.dp))
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${event.date} • ${event.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

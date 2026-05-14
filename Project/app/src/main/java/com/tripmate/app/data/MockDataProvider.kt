package com.tripmate.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tripmate.app.models.*
import com.tripmate.app.network.SupabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object MockDataProvider {
    val trips = mutableStateListOf<Trip>()
    val completedTrips = mutableStateListOf<Trip>()
    val expenses = mutableStateListOf<Expense>()
    val tasks = mutableStateListOf<Task>()
    val events = mutableStateListOf<Event>()
    val members = mutableStateListOf<Member>()
    val notifications = mutableStateListOf<Notification>()
    val travelMemories = mutableStateListOf<TravelMemory>()

    var isDarkMode by mutableStateOf(true)

    var currentUser by mutableStateOf(
        UserProfile(
            id = "u1",
            name = "Demo User",
            status = "Premium Traveler",
            email = "demo@tripmate.com",
            tripsCount = 0,
            countriesCount = 0,
            budgetSpent = "₹0"
        )
    )

    private val scope = CoroutineScope(Dispatchers.Main)

    // Mock Data Definitions
    private val mockTrips = listOf(
        Trip(
            "t1",
            "Goa Trip",
            "Oct 10-15, 2026",
            15000.0,
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80",
            "GOA123"
        ),
        Trip(
            "t2",
            "Munnar Hills Escape",
            "Dec 1-5, 2026",
            22000.0,
            "https://images.unsplash.com/photo-1472396961693-142e6e269027?auto=format&fit=crop&w=1200&q=80",
            "MUNNAR"
        ),
        Trip(
            "t3",
            "Chennai Weekend Trip",
            "Jan 10-12, 2026",
            8000.0,
            "https://images.unsplash.com/photo-1529253355930-ddbe423a2ac7?auto=format&fit=crop&w=1200&q=80",
            "CHENNAI"
        )
    )

    private val mockCompletedTrips = listOf(
        Trip(
            "c1",
            "Goa Beach Escape",
            "Completed: Sep 18-22, 2025",
            18000.0,
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80",
            "GOA-DONE"
        ),
        Trip(
            "c2",
            "Jaipur Royal Tour",
            "Completed: Feb 3-7, 2025",
            21000.0,
            "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=1200&q=80",
            "JAIPUR-DONE"
        ),
        Trip(
            "c3",
            "Chennai Heritage Walk",
            "Completed: Jan 2-4, 2025",
            9000.0,
            "https://images.unsplash.com/photo-1556155092-490a1ba16284?auto=format&fit=crop&w=1200&q=80",
            "CHENNAI-DONE"
        )
    )

    private val mockExpenses = listOf(
        Expense("e1", "t1", "Food", 500.0, System.currentTimeMillis()),
        Expense("e2", "t1", "Travel", 1200.0, System.currentTimeMillis()),
        Expense("e3", "t1", "Hotel", 3000.0, System.currentTimeMillis())
    )

    private val mockTasks = listOf(
        Task("tk1", "t1", "Pack clothes", false),
        Task("tk2", "t1", "Book tickets", true),
        Task("tk3", "t1", "Carry ID proof", false),
        Task("tk4", "t1", "Plan itinerary", true)
    )

    private val mockEvents = listOf(
        Event("ev1", "t1", "Beach visit", "Oct 11", "10:00 AM"),
        Event("ev2", "t1", "Hotel check-in", "Oct 10", "2:00 PM"),
        Event("ev3", "t1", "Local sightseeing", "Oct 12", "9:00 AM")
    )

    private val mockMembers = listOf(
        Member("m1", "t1", "User A", "usera@tripmate.com", "Admin"),
        Member("m2", "t1", "User B", "userb@tripmate.com", "Member"),
        Member("m3", "t1", "User C", "userc@tripmate.com", "Member")
    )

    private val mockNotifications = listOf(
        Notification("Trip created successfully", "trip", System.currentTimeMillis(), "t1"),
        Notification("Expense added", "expense", System.currentTimeMillis(), "t1"),
        Notification("Task completed", "task", System.currentTimeMillis(), "t1"),
        Notification("New member joined", "member", System.currentTimeMillis(), "t1")
    )

    private val mockMemories = listOf(
        TravelMemory("tm1", "Goa", "Oct 2024", "Goa sunset photo"),
        TravelMemory("tm2", "Manali", "Dec 2024", "Manali mountain view"),
        TravelMemory("tm3", "Chennai", "Jan 2025", "Chennai street food trip")
    )

    init {
        loadData()
    }

    fun loadData() {
        scope.launch {
            // Load Trips
            val remoteTrips = SupabaseRepository.getTrips()
            trips.clear()
            if (remoteTrips.isEmpty()) {
                trips.addAll(mockTrips)
            } else {
                trips.addAll(remoteTrips)
            }

            completedTrips.clear()
            completedTrips.addAll(mockCompletedTrips)

            // Load Profile
            val remoteProfile = SupabaseRepository.getUserProfile("u1")
            if (remoteProfile == null) {
                currentUser = UserProfile(
                    id = "u1",
                    name = "Demo User",
                    status = "Travel Enthusiast",
                    email = "demo@tripmate.com",
                    tripsCount = completedTrips.size,
                    countriesCount = 3,
                    budgetSpent = "₹4700",
                    profileImage = null
                )
            } else {
                currentUser = remoteProfile.copy(tripsCount = completedTrips.size)
            }

            // Load Memories
            val remoteMemories = SupabaseRepository.getTravelMemories()
            travelMemories.clear()
            if (remoteMemories.isEmpty()) {
                travelMemories.addAll(mockMemories)
            } else {
                travelMemories.addAll(remoteMemories)
            }

            // Load Notifications
            val remoteNotifications = SupabaseRepository.getNotifications()
            notifications.clear()
            if (remoteNotifications.isEmpty()) {
                notifications.addAll(mockNotifications)
            } else {
                notifications.addAll(remoteNotifications)
            }

            members.clear()
            members.addAll(mockMembers)
        }
    }

    fun loadTripDetails(tripId: String) {
        scope.launch {
            val remoteExpenses = SupabaseRepository.getExpenses(tripId)
            expenses.clear()
            if (remoteExpenses.isEmpty()) {
                val matchingExpenses = mockExpenses.filter { it.tripId == tripId }
                expenses.addAll(matchingExpenses.ifEmpty { mockExpenses.map { it.copy(id = it.id + "x", tripId = tripId) } })
            } else {
                expenses.addAll(remoteExpenses)
            }

            val remoteTasks = SupabaseRepository.getTasks(tripId)
            tasks.clear()
            if (remoteTasks.isEmpty()) {
                val matchingTasks = mockTasks.filter { it.tripId == tripId }
                tasks.addAll(matchingTasks.ifEmpty { mockTasks.map { it.copy(id = it.id + "x", tripId = tripId) } })
            } else {
                tasks.addAll(remoteTasks)
            }

            val remoteEvents = SupabaseRepository.getEvents(tripId)
            events.clear()
            if (remoteEvents.isEmpty()) {
                val matchingEvents = mockEvents.filter { it.tripId == tripId }
                events.addAll(matchingEvents.ifEmpty { mockEvents.map { it.copy(id = it.id + "x", tripId = tripId) } })
            } else {
                events.addAll(remoteEvents)
            }
        }
    }

    fun addTrip(trip: Trip) {
        trips.add(0, trip)
        scope.launch {
            SupabaseRepository.insertTrip(trip)
            addNotification("New trip to ${trip.destination} created!", "trip")
        }
    }

    fun addExpense(expense: Expense) {
        expenses.add(expense)
        scope.launch {
            SupabaseRepository.insertExpense(expense)
            addNotification("Expense added: ${expense.title}", "expense")
        }
    }

    fun addTask(task: Task) {
        tasks.add(task)
        scope.launch {
            SupabaseRepository.insertTask(task)
            addNotification("Task added: ${task.name}", "task")
        }
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            scope.launch {
                SupabaseRepository.updateTask(task)
            }
        }
    }

    fun addEvent(event: Event) {
        events.add(event)
        scope.launch {
            SupabaseRepository.insertEvent(event)
            addNotification("Event added: ${event.title}", "event")
        }
    }

    fun addMember(member: Member) {
        members.add(member)
        addNotification("${member.name} joined the trip", "trip")
    }

    fun addNotification(message: String, type: String) {
        val notif = Notification(message = message, type = type, timestamp = System.currentTimeMillis())
        notifications.add(0, notif)
        scope.launch {
            SupabaseRepository.insertNotification(notif)
        }
    }

    fun updateProfile(profile: UserProfile) {
        currentUser = profile
        scope.launch {
            SupabaseRepository.updateUserProfile(profile)
        }
    }

    fun addMemory(memory: TravelMemory) {
        travelMemories.add(0, memory)
        scope.launch {
            SupabaseRepository.insertMemory(memory)
        }
    }
}

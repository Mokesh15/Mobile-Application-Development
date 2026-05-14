package com.tripmate.app.models

import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: String,
    val destination: String,
    val date: String,
    val budget: Double,
    val imageUrl: String = "",
    val inviteCode: String = ""
)

@Serializable
data class Expense(
    val id: String,
    val tripId: String,
    val title: String,
    val amount: Double,
    val timestamp: Long
)

@Serializable
data class Task(
    val id: String,
    val tripId: String,
    val name: String,
    var completed: Boolean = false
)

@Serializable
data class Event(
    val id: String,
    val tripId: String,
    val title: String,
    val date: String,
    val time: String
)

@Serializable
data class Member(
    val id: String,
    val tripId: String,
    val name: String,
    val email: String = "",
    val role: String = "Member",
    val joinedDate: String = "May 2024"
)

@Serializable
data class Notification(
    val message: String,
    val type: String,
    val timestamp: Long,
    val tripId: String = ""
)

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val status: String,
    val email: String,
    val tripsCount: Int,
    val countriesCount: Int,
    val budgetSpent: String,
    val profileImage: String? = null
)

@Serializable
data class TravelMemory(
    val id: String,
    val city: String,
    val date: String,
    val comment: String
)

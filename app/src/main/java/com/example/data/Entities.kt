package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "meetings")
data class ClientMeeting(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val clientEmail: String,
    val title: String,
    val description: String,
    val dateTime: Long, // timestamp
    val status: String, // "Scheduled", "Completed", "Cancelled"
    val meetingLink: String,
    val notificationSent: Boolean = false
) : Serializable

@Entity(tableName = "inquiries")
data class ClientInquiry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val clientEmail: String,
    val companyName: String,
    val subject: String,
    val message: String,
    val receivedTime: Long,
    val status: String, // "New", "Under Review", "Replied"
    val replyMessage: String? = null
) : Serializable

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val clientEmail: String,
    val serviceType: String, // "Cloud Architecture", "Salesforce Consulting", "Software Engineering", "AI Transformation"
    val dateTime: Long,
    val durationMinutes: Int = 30,
    val notes: String,
    val status: String, // "Pending", "Confirmed", "Completed"
    val isEmailSent: Boolean = false
) : Serializable

@Entity(tableName = "email_logs")
data class EmailLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipient: String,
    val subject: String,
    val body: String,
    val sentTime: Long,
    val triggerEvent: String, // "Meeting Confirmed", "Inquiry Reply", "Appointment Reminder", "Auto-Notification"
    val status: String // "Sent", "Failed"
) : Serializable

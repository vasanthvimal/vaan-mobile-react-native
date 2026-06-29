package com.example.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AppViewModel"
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database.appDao())

    // UI flows backed reactively by Room database streams
    val meetings: StateFlow<List<ClientMeeting>> = repository.allMeetings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inquiries: StateFlow<List<ClientInquiry>> = repository.allInquiries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<Appointment>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emailLogs: StateFlow<List<EmailLog>> = repository.allEmailLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Setting for automatic email dispatches (defaults to true)
    private val _autoEmailEnabled = MutableStateFlow(true)
    val autoEmailEnabled: StateFlow<Boolean> = _autoEmailEnabled.asStateFlow()

    // AI Status fields for state management
    private val _aiDraftState = MutableStateFlow<AiDraftState>(AiDraftState.Idle)
    val aiDraftState: StateFlow<AiDraftState> = _aiDraftState.asStateFlow()

    private val _notificationPermissionPrompt = MutableStateFlow(false)
    val notificationPermissionPrompt: StateFlow<Boolean> = _notificationPermissionPrompt.asStateFlow()

    // Persistent SharedPreferences for Favorites/Bookmarks
    private val prefs = application.getSharedPreferences("vaan_prefs", Context.MODE_PRIVATE)

    private val _bookmarkedArticles = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedArticles: StateFlow<Set<String>> = _bookmarkedArticles.asStateFlow()

    private val _bookmarkedServices = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedServices: StateFlow<Set<String>> = _bookmarkedServices.asStateFlow()

    // Chatbot States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("model", "Hello! I am VaanAI, your virtual consultant. How can I assist you with VAAN Consulting's cloud, data, and agility platform architectures today?")
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    init {
        // Create the notification channel
        createNotificationChannel()
        // Load initial seed data if DB is empty to present a polished first-use dashboard
        seedInitialDataIfNecessary()
        
        // Load bookmarks from persistent storage
        _bookmarkedArticles.value = prefs.getStringSet("bookmarked_articles", emptySet()) ?: emptySet()
        _bookmarkedServices.value = prefs.getStringSet("bookmarked_services", emptySet()) ?: emptySet()
    }

    fun setAutoEmailEnabled(enabled: Boolean) {
        _autoEmailEnabled.value = enabled
    }

    fun dismissNotificationPrompt() {
        _notificationPermissionPrompt.value = false
    }

    fun triggerPermissionPrompt() {
        _notificationPermissionPrompt.value = true
    }

    // --- Create / Update operations ---

    fun scheduleMeeting(
        clientName: String,
        clientEmail: String,
        title: String,
        description: String,
        dateTime: Long,
        meetingLink: String
    ) {
        viewModelScope.launch {
            val meeting = ClientMeeting(
                clientName = clientName,
                clientEmail = clientEmail,
                title = title,
                description = description,
                dateTime = dateTime,
                status = "Scheduled",
                meetingLink = meetingLink,
                notificationSent = false
            )
            val meetingId = repository.insertMeeting(meeting)
            
            // Trigger automated email notification if enabled
            if (_autoEmailEnabled.value) {
                simulateAndLogAutomatedEmail(
                    recipient = clientEmail,
                    subject = "Confirmed: Vaan Consulting Scheduled Meeting - $title",
                    body = """
                        Dear $clientName,
                        
                        This is an automated confirmation of our scheduled meeting.
                        
                        Meeting: $title
                        Description: $description
                        Time: ${formatDateTime(dateTime)}
                        Google Meet Video Link: $meetingLink
                        
                        We look forward to speaking with you. If you need to make changes, please reschedule via our client portal or reply to this message.
                        
                        Best regards,
                        Vaan Consulting Support
                        https://www.vaanconsulting.com/
                    """.trimIndent(),
                    triggerEvent = "Meeting Scheduled",
                    onSuccess = {
                        viewModelScope.launch {
                            repository.updateMeeting(meeting.copy(id = meetingId.toInt(), notificationSent = true))
                        }
                    }
                )
            }
        }
    }

    fun cancelMeeting(meeting: ClientMeeting) {
        viewModelScope.launch {
            val updated = meeting.copy(status = "Cancelled")
            repository.updateMeeting(updated)

            if (_autoEmailEnabled.value) {
                simulateAndLogAutomatedEmail(
                    recipient = meeting.clientEmail,
                    subject = "Cancelled: Vaan Consulting Scheduled Meeting - ${meeting.title}",
                    body = """
                        Dear ${meeting.clientName},
                        
                        This notification is to confirm that our scheduled meeting "${meeting.title}" has been cancelled.
                        
                        If this was in error, or you would like to reschedule, please feel free to create a new appointment slot in your Vaan Client App.
                        
                        Warm regards,
                        Vaan Consulting Operations
                        https://www.vaanconsulting.com/
                    """.trimIndent(),
                    triggerEvent = "Meeting Cancelled"
                )
            }
        }
    }

    fun submitInquiry(
        clientName: String,
        clientEmail: String,
        companyName: String,
        subject: String,
        message: String
    ) {
        viewModelScope.launch {
            val inquiry = ClientInquiry(
                clientName = clientName,
                clientEmail = clientEmail,
                companyName = companyName,
                subject = subject,
                message = message,
                receivedTime = System.currentTimeMillis(),
                status = "New"
            )
            repository.insertInquiry(inquiry)

            // Trigger immediate automated auto-acknowledgement email
            if (_autoEmailEnabled.value) {
                simulateAndLogAutomatedEmail(
                    recipient = clientEmail,
                    subject = "Received: Vaan Consulting - $subject",
                    body = """
                        Dear $clientName,
                        
                        We have received your technical inquiry regarding "$subject" for $companyName.
                        
                        One of our lead IT consultants will review your brief and follow up within one business hour.
                        
                        Inquiry Message Summary:
                        "$message"
                        
                        Sincerely,
                        Customer Relations Team
                        Vaan Consulting
                        https://www.vaanconsulting.com/
                    """.trimIndent(),
                    triggerEvent = "Inquiry Auto-Response"
                )
            }

            // Trigger real email dispatch to vaanconsulting@gmail.com
            launch {
                val success = FormSubmitHelper.sendSubmission(
                    name = clientName,
                    email = clientEmail,
                    company = companyName,
                    service = subject,
                    message = message,
                    subjectLine = "VAAN Consulting Mobile - New Inquiry: $subject from $clientName"
                )
                if (success) {
                    Log.i("AppViewModel", "Inquiry email triggered successfully to vaanconsulting@gmail.com")
                } else {
                    Log.e("AppViewModel", "Failed to trigger inquiry email to vaanconsulting@gmail.com")
                }
            }
        }
    }

    fun submitInquiryReply(inquiry: ClientInquiry, replyText: String) {
        viewModelScope.launch {
            val updated = inquiry.copy(
                status = "Replied",
                replyMessage = replyText
            )
            repository.updateInquiry(updated)

            simulateAndLogAutomatedEmail(
                recipient = inquiry.clientEmail,
                subject = "Re: ${inquiry.subject} - Vaan Consulting Reply",
                body = replyText,
                triggerEvent = "Inquiry Reply"
            )
        }
    }

    fun createAppointment(
        clientName: String,
        clientEmail: String,
        serviceType: String,
        dateTime: Long,
        durationMinutes: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val appt = Appointment(
                clientName = clientName,
                clientEmail = clientEmail,
                serviceType = serviceType,
                dateTime = dateTime,
                durationMinutes = durationMinutes,
                notes = notes,
                status = "Pending"
            )
            val apptId = repository.insertAppointment(appt)

            if (_autoEmailEnabled.value) {
                simulateAndLogAutomatedEmail(
                    recipient = clientEmail,
                    subject = "Booking Received: Vaan Consulting - $serviceType",
                    body = """
                        Dear $clientName,
                        
                        Thank you for booking a technical consulting appointment with us.
                        
                        Service Type: $serviceType
                        Proposed Time: ${formatDateTime(dateTime)} ($durationMinutes Minutes)
                        Proposed Notes: $notes
                        
                        Your appointment request is currently "Pending" confirmation. A Vaan account manager will review the requirements and confirm the slot shortly. An automated confirmation email will follow.
                        
                        Warm regards,
                        The Team at Vaan Consulting
                        https://www.vaanconsulting.com/
                    """.trimIndent(),
                    triggerEvent = "Appointment Booking Request Received"
                )
            }

            // Trigger real email dispatch to vaanconsulting@gmail.com for session registration
            launch {
                val success = FormSubmitHelper.sendSubmission(
                    name = clientName,
                    email = clientEmail,
                    company = "Discovery Call Booking",
                    service = serviceType,
                    message = """
                        Booking Request Details:
                        - Service Category: $serviceType
                        - Proposed Date/Time: ${formatDateTime(dateTime)}
                        - Proposed Duration: $durationMinutes Minutes
                        - Additional Client Notes: $notes
                    """.trimIndent(),
                    subjectLine = "VAAN Consulting Mobile - Discovery Call Booking: $serviceType from $clientName"
                )
                if (success) {
                    Log.i("AppViewModel", "Booking email triggered successfully to vaanconsulting@gmail.com")
                } else {
                    Log.e("AppViewModel", "Failed to trigger booking email to vaanconsulting@gmail.com")
                }
            }
        }
    }

    fun confirmAppointment(appointment: Appointment) {
        viewModelScope.launch {
            val updated = appointment.copy(status = "Confirmed", isEmailSent = true)
            repository.updateAppointment(updated)

            if (_autoEmailEnabled.value) {
                simulateAndLogAutomatedEmail(
                    recipient = appointment.clientEmail,
                    subject = "CONFIRMED: Vaan Technical Discovery - ${appointment.serviceType}",
                    body = """
                        Dear ${appointment.clientName},
                        
                        We are pleased to confirm your technical discovery session with Vaan Consulting.
                        
                        Session Category: ${appointment.serviceType}
                        Confirmed Time: ${formatDateTime(appointment.dateTime)} (${appointment.durationMinutes} Minutes)
                        Meeting Access: Google Meet link is attached to your calendar.
                        Briefing Notes: ${appointment.notes}
                        
                        Our consulting lead will join the conference call prepared to discuss your architectural roadmap. Please ensure any technical designs or repository accesses are shared in advance.
                        
                        See you there!
                        
                        Operations Desk
                        Vaan Consulting
                        https://www.vaanconsulting.com/
                    """.trimIndent(),
                    triggerEvent = "Appointment Confirmed"
                )
            }
        }
    }

    fun completeAppointment(appointment: Appointment) {
        viewModelScope.launch {
            val updated = appointment.copy(status = "Completed")
            repository.updateAppointment(updated)
        }
    }

    // --- Gemini AI Drafting Flow ---

    fun generateAIEmailDraftForInquiry(inquiry: ClientInquiry) {
        viewModelScope.launch {
            _aiDraftState.value = AiDraftState.Loading
            val prompt = """
                You are a senior IT consulting specialist at Vaan Consulting (https://www.vaanconsulting.com/).
                Draft a highly professional, welcoming, and intelligent response to a client's technical inquiry.
                
                Client Name: ${inquiry.clientName}
                Company: ${inquiry.companyName}
                Inquiry Subject: ${inquiry.subject}
                Inquiry Message: ${inquiry.message}
                
                Write a response addressing their subject, providing modern high-level consultative suggestions (e.g., AWS Cloud architecture, low-latency Snowflake pipelines, secure offline-first Android apps, enterprise React web solutions), and proposing a 15-minute alignment call. Ensure it is written in a professional, courteous corporate consulting tone. Do not use generic placeholders. Signature should be "Vaan Consulting Expert Team".
            """.trimIndent()

            val systemInstruction = "You are a professional IT consultant at Vaan Consulting drafting technical client replies."
            val draft = GeminiHelper.generateDraft(prompt, systemInstruction)
            _aiDraftState.value = AiDraftState.Success(draft)
        }
    }

    fun generateAIEmailDraftForAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _aiDraftState.value = AiDraftState.Loading
            val prompt = """
                You are a principal systems engineer at Vaan Consulting.
                Draft a technical consultation briefing email for a newly confirmed appointment.
                
                Client Name: ${appointment.clientName}
                Service Topic: ${appointment.serviceType}
                Appointment Notes: ${appointment.notes}
                Appointment Date: ${formatDateTime(appointment.dateTime)}
                
                Write a brief, highly technical preparation email outlining what files, accesses, or goals the client should think about prior to this appointment. Use bullet points for structural clarity. Signature should be "Technical Advisory, Vaan Consulting".
            """.trimIndent()

            val systemInstruction = "You are a lead technical architect at Vaan Consulting drafting a preparation brief."
            val draft = GeminiHelper.generateDraft(prompt, systemInstruction)
            _aiDraftState.value = AiDraftState.Success(draft)
        }
    }

    fun resetAiState() {
        _aiDraftState.value = AiDraftState.Idle
    }

    // --- Notification & Dispatch Simulation Helper ---

    private suspend fun simulateAndLogAutomatedEmail(
        recipient: String,
        subject: String,
        body: String,
        triggerEvent: String,
        onSuccess: (() -> Unit)? = null
    ) {
        withContext(Dispatchers.IO) {
            val log = EmailLog(
                recipient = recipient,
                subject = subject,
                body = body,
                sentTime = System.currentTimeMillis(),
                triggerEvent = triggerEvent,
                status = "Sent"
            )
            repository.insertEmailLog(log)
            onSuccess?.invoke()

            // Dispatch native android notification
            triggerAndroidSystemNotification(recipient, subject)
        }
    }

    private fun triggerAndroidSystemNotification(recipient: String, subject: String) {
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = "Vaan Auto-Email Sent"
        val message = "To: $recipient\nSub: $subject"

        val builder = NotificationCompat.Builder(context, "vaan_email_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: SecurityException) {
            Log.e(TAG, "Notification permission missing on API 33+", e)
            _notificationPermissionPrompt.value = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fire notification", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = getApplication<Application>().applicationContext
            val name = "Vaan Email Dispatches"
            val descriptionText = "Alerts notifying when automated email communications are triggered for clients."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("vaan_email_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun seedInitialDataIfNecessary() {
        viewModelScope.launch(Dispatchers.IO) {
            // Check if db is empty
            val currentMeetings = repository.allMeetings.first()
            val currentInquiries = repository.allInquiries.first()
            val currentAppointments = repository.allAppointments.first()

            if (currentMeetings.isEmpty() && currentInquiries.isEmpty() && currentAppointments.isEmpty()) {
                Log.d(TAG, "Database is empty. Seeding representative consulting demo data...")

                // Current time milestones
                val now = System.currentTimeMillis()
                val oneHour = 3600000L
                val oneDay = 86400000L

                // 1. Seed meetings
                repository.insertMeeting(
                    ClientMeeting(
                        clientName = "Sarah Jenkins",
                        clientEmail = "s.jenkins@acmehealth.com",
                        title = "AWS Serverless Scaling Discovery",
                        description = "Architectural review of serverless transition for health records APIs.",
                        dateTime = now + oneHour * 4,
                        status = "Scheduled",
                        meetingLink = "https://meet.google.com/abc-defg-hij",
                        notificationSent = true
                    )
                )
                repository.insertMeeting(
                    ClientMeeting(
                        clientName = "David Chen",
                        clientEmail = "dchen@zenithretail.co",
                        title = "Data Platform Architecture & Strategy Alignment",
                        description = "Reviewing Snowflake ingestion and syncing schedules with engineering teams.",
                        dateTime = now + oneDay * 2,
                        status = "Scheduled",
                        meetingLink = "https://meet.google.com/xyz-qprs-tuv",
                        notificationSent = true
                    )
                )

                // 2. Seed inquiries
                repository.insertInquiry(
                    ClientInquiry(
                        clientName = "Elena Rostova",
                        clientEmail = "elena.r@innovatefintech.io",
                        companyName = "Innovate Fintech Ltd",
                        subject = "Kubernetes Clustering Setup & Staff Augmentation",
                        message = "We need an external cloud engineering partner to help us refactor our legacy banking stack into modular microservices using AWS EKS. Do you have available Kubernetes experts for a 3-month contract?",
                        receivedTime = now - oneHour * 3,
                        status = "New"
                    )
                )
                repository.insertInquiry(
                    ClientInquiry(
                        clientName = "Marcus Brody",
                        clientEmail = "mbrody@nexustransit.org",
                        companyName = "Nexus Transit Systems",
                        subject = "Legacy Database Migration Assessment",
                        message = "Seeking consulting advice on migrating our mainframe DB2 schemas to PostgreSQL in GCP with zero-downtime replications.",
                        receivedTime = now - oneDay,
                        status = "Replied",
                        replyMessage = "Dear Marcus, Vaan Consulting would be delighted to perform a DB migration assessment. We recommend GCP Database Migration Service (DMS) for PostgreSQL targets..."
                    )
                )

                // 3. Seed appointments
                repository.insertAppointment(
                    Appointment(
                        clientName = "Robert Vance",
                        clientEmail = "rvance@vancerefrigeration.com",
                        serviceType = "Cloud Architecture",
                        dateTime = now + oneDay * 3 + oneHour * 2,
                        durationMinutes = 45,
                        notes = "Migration of warehouse inventory tracking database to Azure SQL.",
                        status = "Confirmed",
                        isEmailSent = true
                    )
                )
                repository.insertAppointment(
                    Appointment(
                        clientName = "Olivia Vance",
                        clientEmail = "olivia@vancerefrigeration.com",
                        serviceType = "AI Transformation",
                        dateTime = now + oneDay * 5,
                        durationMinutes = 60,
                        notes = "Explore Gemini models to parse client support inquiries and automate replies.",
                        status = "Pending",
                        isEmailSent = false
                    )
                )

                // 4. Seed initial email logs
                repository.insertEmailLog(
                    EmailLog(
                        recipient = "s.jenkins@acmehealth.com",
                        subject = "Confirmed: Vaan Consulting Scheduled Meeting - AWS Serverless Scaling Discovery",
                        body = "Pre-filled schedule confirmation message sent.",
                        sentTime = now - oneHour * 2,
                        triggerEvent = "Meeting Scheduled",
                        status = "Sent"
                    )
                )
                repository.insertEmailLog(
                    EmailLog(
                        recipient = "rvance@vancerefrigeration.com",
                        subject = "CONFIRMED: Vaan Technical Discovery - Cloud Architecture",
                        body = "Vance Refrigeration AWS setup confirmation dispatch.",
                        sentTime = now - oneHour,
                        triggerEvent = "Appointment Confirmed",
                        status = "Sent"
                    )
                )
            }
        }
    }

    private fun formatDateTime(timestamp: Long): String {
        return java.text.SimpleDateFormat("MMM dd, yyyy - hh:mm a", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    }

    // --- Bookmarking / Favorites Operations ---

    fun toggleArticleBookmark(articleId: String) {
        val current = _bookmarkedArticles.value.toMutableSet()
        if (current.contains(articleId)) {
            current.remove(articleId)
        } else {
            current.add(articleId)
        }
        _bookmarkedArticles.value = current
        prefs.edit().putStringSet("bookmarked_articles", current).apply()
    }

    fun toggleServiceBookmark(serviceId: String) {
        val current = _bookmarkedServices.value.toMutableSet()
        if (current.contains(serviceId)) {
            current.remove(serviceId)
        } else {
            current.add(serviceId)
        }
        _bookmarkedServices.value = current
        prefs.edit().putStringSet("bookmarked_services", current).apply()
    }

    // --- AI Chatbot Operations ---

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            // Append user message immediately
            val currentList = _chatMessages.value.toMutableList()
            currentList.add(ChatMessage("user", trimmed))
            _chatMessages.value = currentList

            _isChatLoading.value = true

            // Generate AI Response (uses chatHistory including the latest user message)
            val history = currentList.dropLast(1).map { Pair(it.sender, it.text) }
            val response = GeminiHelper.getChatResponse(trimmed, history)

            _isChatLoading.value = false

            // Append response message
            val updatedList = _chatMessages.value.toMutableList()
            updatedList.add(ChatMessage("model", response))
            _chatMessages.value = updatedList
        }
    }

    fun clearChatHistory() {
        _chatMessages.value = listOf(
            ChatMessage("model", "Hello! I am VaanAI, your virtual consultant. How can I assist you with VAAN Consulting's cloud, data, and agility platform architectures today?")
        )
    }
}

data class ChatMessage(val sender: String, val text: String)

sealed interface AiDraftState {
    object Idle : AiDraftState
    object Loading : AiDraftState
    data class Success(val draft: String) : AiDraftState
}

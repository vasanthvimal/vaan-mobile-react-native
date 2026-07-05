package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Appointment
import com.example.data.ClientInquiry
import com.example.data.ClientMeeting
import com.example.data.EmailLog
import com.example.viewmodel.AiDraftState
import com.example.viewmodel.AppViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.*

// =========================================================================
// VAAN CONSULTING STATIC MODELS & SEED DATA
// =========================================================================

data class TechArticle(
    val id: String,
    val title: String,
    val category: String,
    val readTime: String,
    val date: String,
    val summary: String,
    val content: String
)

data class VaanService(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val subtitle: String,
    val description: String,
    val platforms: List<String>,
    val details: List<String>
)

data class VaanCapabilityItem(
    val icon: ImageVector,
    val title: String,
    val desc: String,
    val tags: List<String>
)

val articlesList = listOf(
    TechArticle(
        id = "fabric_analytics",
        title = "Unlocking Real-Time Analytics with Microsoft Fabric",
        category = "Data Platform",
        readTime = "5 min read",
        date = "June 25, 2026",
        summary = "Learn how to orchestrate low-latency pipeline streams using Microsoft Fabric and Lakehouse storage architectures.",
        content = "Microsoft Fabric has emerged as a major player for modern unified enterprise data platforms. By consolidating data lakes, data engineering, data warehouses, and power visualization under a single 'OneLake' SaaS engine, Fabric eliminates the friction of traditional data silos.\n\nKey architectural takeaways for successful implementation:\n1. Unified Storage: OneLake acts as the single source of truth, organizing data in standard Delta Parquet formats.\n2. Medallion Topology: Build Bronze, Silver, and Gold delta tables to systematically clean and refine streams.\n3. Direct Lake Mode: Stream massive datasets from OneLake directly into PowerBI without importing or querying live engines, resulting in a 30% reduction in processing overhead.\n\nOur engineering tests at Vaan show that migrating pipelines to Fabric achieves dramatic reductions in maintenance complexity, specifically for logistics and retail enterprises."
    ),
    TechArticle(
        id = "safe_regulated",
        title = "SAFe 6.0 in Regulated Enterprises: A Practitioner’s Guide",
        category = "Bespoke Consulting",
        readTime = "8 min read",
        date = "May 18, 2026",
        summary = "How to systematically scale agile practices, compliance, security, and velocity in highly regulated sectors.",
        content = "Implementing agile methodologies like SAFe 6.0 in highly regulated environments (such as banking, automotive, and nuclear energy) is often considered an oxymoron. However, compliance and agility can coexist harmoniously when built on trust and automation.\n\nThree pillars of compliant scale:\n1. Automate Governance: Shift security and automated tests left. Every commit should generate compliance logs.\n2. Lean Portfolio Management (LPM): Align funding directly with strategic value streams rather than rigid projects.\n3. System Demo Involvements: Ensure regulatory compliance officers attend quarterly demos to provide continuous feedback rather than late audits.\n\nBy adopting a value-stream mindset, banking groups have successfully improved development throughput while satisfying stringent audit requirements."
    ),
    TechArticle(
        id = "mainframe_postgres",
        title = "Migrating Mainframe DB2 to PostgreSQL with Zero Downtime",
        category = "Cloud & Digital",
        readTime = "12 min read",
        date = "April 05, 2026",
        summary = "A detailed technical guide on real-time CDC replication strategies on AWS RDS and GCP Cloud SQL.",
        content = "Mainframe modernization requires extreme care, especially when migrating core operational databases. Traditional 'lift and shift' operations fail due to business disruption. The solution lies in Change Data Capture (CDC) replication topologies.\n\nWe recommend the following migration pathway:\n1. Schema Conversion: Use Schema Conversion tools to convert legacy DB2 schemas, indexing, and store procedures to PostgreSQL.\n2. Real-Time Synchronization: Establish a CDC sync layer (e.g., using GCP DMS or AWS DMS) to replicate transaction logs continuously.\n3. Dual-Run Phase: Run both systems in parallel, routing a percentage of query traffic to the cloud Postgres target.\n4. Cutover: Switch the write endpoint to PostgreSQL once replication lag hits sub-millisecond levels.\n\nOur teams routinely deliver migrations that reduce annual licensing fees by up to 70% while improving transaction speeds in financial portfolios."
    ),
    TechArticle(
        id = "mobile_offline_first",
        title = "Designing Robust Offline-First Mobile Architectures",
        category = "Mobile Dev",
        readTime = "8 min read",
        date = "June 18, 2026",
        summary = "How to build resilient Android applications using Room Database, Kotlin Flow, and WorkManager sync engines.",
        content = "Mobile apps built for field operations or enterprise logistics must remain fully functional regardless of connectivity status. Traditional app designs rely heavily on synchronous REST APIs, which break immediately under poor cellular coverage.\n\nOur recommended offline-first architecture:\n1. Local Storage First: All user read and write actions interact strictly with the local Room Database. The UI observes database flows.\n2. Scheduled Syncing: Utilize Android WorkManager to schedule background sync tasks that retry automatically when connectivity is restored.\n3. Conflict Resolution Strategy: Implement deterministic timestamp-based or client-wins policies to handle concurrent database writes.\n4. Network Compression: Compress outgoing synchronization payloads using Protobuf or GZIP to reduce bandwidth overhead.\n\nBy prioritizing local database flows, applications improve latency to near-zero milliseconds and maintain complete functional stability in remote areas."
    ),
    TechArticle(
        id = "ai_auto_categorization",
        title = "AI Support Automation using LLMs and Structured Parsing",
        category = "Web Dev",
        readTime = "7 min read",
        date = "February 22, 2026",
        summary = "Streamline client support queues by building automated text categorization pipelines with LLMs like Gemini.",
        content = "Modern support teams are often overwhelmed by bulk support inquiries. Generative AI can assist by classifying incoming tickets, extracting actionable metadata (company names, products), and drafting high-quality email replies inside customized enterprise web platforms.\n\nThe processing architecture:\n1. Ingestion: Catch inquiries from webhooks or SMTP listeners.\n2. Structured Prompting: Prompt models with structured JSON schemas to enforce output standards (e.g. sentiment score, urgency, category).\n3. Human-in-the-Loop Review: Present the drafted responses to human operators inside a responsive web dashboard for verification before sending.\n\nThis automated approach ensures critical enterprise inquiries receive initial professional responses in less than one minute, resulting in higher retention."
    )
)

val servicesList = listOf(
    VaanService(
        id = "data_platform",
        title = "Data Platform Architecture & Strategy",
        icon = Icons.Default.Storage,
        subtitle = "Modern Warehouses & Low-Latency Pipelines",
        description = "Transform raw enterprise data streams into decision-ready business intelligence. We design scalable analytics pipelines and secure data lakehouses using the latest cloud warehouse platforms.",
        platforms = listOf("Snowflake", "Databricks", "Microsoft Fabric", "PostgreSQL"),
        details = listOf(
            "Real-time ETL/ELT pipeline design and streaming telemetry",
            "Modern data lakehouse deployment (Delta Lake and Parquet)",
            "Database modernizations & zero-downtime DB migrations",
            "Analytical reporting optimization & cost control"
        )
    ),
    VaanService(
        id = "cloud_transform",
        title = "Cloud & Digital Transformation",
        icon = Icons.Default.Cloud,
        subtitle = "Multi-Cloud Strategy (AWS, Azure, GCP)",
        description = "We design resilient, secure, and cost-optimised cloud backends. From containerisation to serverless deployments, we orchestrate zero-downtime migrations that scale under regulatory compliance.",
        platforms = listOf("AWS", "Azure", "GCP", "Kubernetes"),
        details = listOf(
            "Multi-region architecture design with active-active scaling",
            "Infrastructure as Code (IaC) using Terraform and Ansible",
            "Kubernetes container orchestration (EKS, AKS, GKE)",
            "Compliance alignment & FinOps cloud cost optimisation"
        )
    ),
    VaanService(
        id = "mobile_dev",
        title = "Mobile Application Development",
        icon = Icons.Default.PhoneAndroid,
        subtitle = "High-Performance Native & Cross-Platform Apps",
        description = "We build secure, offline-first mobile solutions. Our engineering ensures smooth background synchronization, local encryption, and highly responsive user interfaces with Jetpack Compose.",
        platforms = listOf("Android", "Kotlin", "Jetpack Compose", "SQLite / Room"),
        details = listOf(
            "Offline-first databases with background network sync engines",
            "Secure local storage, biometric auth, and encryption",
            "Highly responsive Material 3 reactive animations",
            "Play Store publishing and telemetry monitoring pipelines"
        )
    ),
    VaanService(
        id = "web_dev",
        title = "Web Application Development",
        icon = Icons.Default.Code,
        subtitle = "Enterprise Web Applications & API Gateways",
        description = "Build scalable, secure web services and customer portals. We optimize backend microservices and construct modern web applications designed for high load.",
        platforms = listOf("React", "TypeScript", "Node.js", "GraphQL / REST APIs"),
        details = listOf(
            "Low-latency reactive single page applications (SPAs)",
            "Secure API integration with high-performance cache layers",
            "Role-based authentication & enterprise SSO integrations",
            "Automated testing, CI/CD pipelines, and cloud deployments"
        )
    ),
    VaanService(
        id = "bespoke_consulting",
        title = "Bespoke Technology Consulting / Other",
        icon = Icons.Default.TrendingUp,
        subtitle = "Enterprise Agility, Mentoring & Due Diligence",
        description = "Bridging the gap between software developers and executive vision. We coach teams on agile delivery, product ownership, and lean portfolio management (SAFe 6.0) to maximize value.",
        platforms = listOf("SAFe 6", "Agile Coaching", "Tech Audits", "Mentorship"),
        details = listOf(
            "SAFe 6.0 transformation roadmap formulation & training",
            "Lean Portfolio Management (LPM) alignment & funding workshops",
            "Agile Release Train (ART) facilitation & PI Planning preparation",
            "Architecture due diligence and technical audit reports"
        )
    )
)

// =========================================================================
// REUSABLE BRAND COMPONENTS (EXACT LOGO & TYPOGRAPHY)
// =========================================================================

@Composable
fun VaanBrandLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val strokeWidth = w * 0.12f
        val tealColor = Color(0xFF14B8A6) // Bright Teal/Cyan (#14B8A6)
        val goldColor = Color(0xFFF59E0B) // Amber/Gold Vertex (#F59E0B)
        
        // Left slant of V
        drawLine(
            color = tealColor,
            start = Offset(w * 0.20f, h * 0.22f),
            end = Offset(w * 0.50f, h * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        
        // Right slant of V
        drawLine(
            color = tealColor,
            start = Offset(w * 0.80f, h * 0.22f),
            end = Offset(w * 0.50f, h * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        
        // Connected orange/gold consulting vertex dot
        drawCircle(
            color = goldColor,
            radius = w * 0.11f,
            center = Offset(w * 0.50f, h * 0.78f)
        )
    }
}

@Composable
fun VaanBrandText(fontSizeSp: androidx.compose.ui.unit.TextUnit = 15.sp, isDark: Boolean = true) {
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    Text(
        text = buildAnnotatedString {
            append("VAAN")
            withStyle(style = SpanStyle(color = Color(0xFF2DD4BF))) {
                append(".")
            }
            append("CONSULTING")
        },
        fontSize = fontSizeSp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        color = textColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: AppViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf("home") } // "home", "services", "insights", "chatbot", "bookings"
    var selectedServiceCategoryForBooking by remember { mutableStateOf("") }

    val meetings by viewModel.meetings.collectAsState()
    val inquiries by viewModel.inquiries.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val emailLogs by viewModel.emailLogs.collectAsState()
    val autoEmailEnabled by viewModel.autoEmailEnabled.collectAsState()
    val permissionPromptVisible by viewModel.notificationPermissionPrompt.collectAsState()

    val bookmarkedArticles by viewModel.bookmarkedArticles.collectAsState()
    val bookmarkedServices by viewModel.bookmarkedServices.collectAsState()

    // Splash Screen timeout
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2200)
        showSplash = false
    }

    if (permissionPromptVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissNotificationPrompt() },
            title = { Text("Enable Delivery Alerts") },
            text = { Text("To see real-time alerts whenever automated notifications are dispatched to clients, please ensure system notifications are permitted.") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissNotificationPrompt() }) {
                    Text("Got It")
                }
            }
        )
    }

    if (showSplash) {
        SplashScreen(onDismiss = { showSplash = false })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VaanBrandLogo(
                                modifier = Modifier
                                    .size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                VaanBrandText(fontSizeSp = 15.sp, isDark = true)
                                Text(
                                    text = "Enterprise Cloud & Data Hub",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (autoEmailEnabled) Color(0xFF10B981) else Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (autoEmailEnabled) "SMTP Online" else "SMTP Paused",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (autoEmailEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentTab == "home",
                        onClick = { currentTab = "home" },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = currentTab == "services",
                        onClick = { currentTab = "services" },
                        icon = { Icon(Icons.Default.CloudQueue, contentDescription = "Services") },
                        label = { Text("Services", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_services")
                    )
                    NavigationBarItem(
                        selected = currentTab == "insights",
                        onClick = { currentTab = "insights" },
                        icon = { Icon(Icons.Default.Language, contentDescription = "Insights") },
                        label = { Text("Blog", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_insights")
                    )
                    NavigationBarItem(
                        selected = currentTab == "chatbot",
                        onClick = { currentTab = "chatbot" },
                        icon = { Icon(Icons.Default.Chat, contentDescription = "VaanAI Chat") },
                        label = { Text("VaanAI Chat", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_chatbot")
                    )
                    NavigationBarItem(
                        selected = currentTab == "bookings",
                        onClick = { currentTab = "bookings" },
                        icon = { Icon(Icons.Default.Schedule, contentDescription = "Bookings Portal") },
                        label = { Text("Bookings", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_bookings")
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    "home" -> HomeScreen(
                        meetings = meetings,
                        inquiries = inquiries,
                        appointments = appointments,
                        emailLogs = emailLogs,
                        autoEmailEnabled = autoEmailEnabled,
                        onNavigateToTab = { currentTab = it }
                    )
                    "services" -> ServicesScreen(
                        bookmarkedServices = bookmarkedServices,
                        onToggleBookmark = { viewModel.toggleServiceBookmark(it) },
                        onBookService = { serviceTitle ->
                            selectedServiceCategoryForBooking = serviceTitle
                            currentTab = "bookings"
                        }
                    )
                    "insights" -> InsightsScreen(
                        bookmarkedArticles = bookmarkedArticles,
                        onToggleBookmark = { viewModel.toggleArticleBookmark(it) }
                    )
                    "chatbot" -> VaanAiChatScreen(viewModel = viewModel)
                    "bookings" -> PortalScreen(
                        meetings = meetings,
                        inquiries = inquiries,
                        appointments = appointments,
                        emailLogs = emailLogs,
                        autoEmailEnabled = autoEmailEnabled,
                        onToggleAutoEmail = { viewModel.setAutoEmailEnabled(it) },
                        onNavigateToTab = { currentTab = it },
                        selectedServiceForBooking = selectedServiceCategoryForBooking,
                        onClearBookingSelection = { selectedServiceCategoryForBooking = "" },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

// =========================================================================
// 1. SPLASH SCREEN COMPOSABLE
// =========================================================================
@Composable
fun SplashScreen(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Vaan Midnight
                        Color(0xFF020617)
                    )
                )
            )
            .clickable(onClick = onDismiss), // Tap to skip splash immediately
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            VaanBrandLogo(
                modifier = Modifier
                    .size(96.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            VaanBrandText(fontSizeSp = 28.sp, isDark = true)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enterprise cloud & data, engineered to move.",
                color = Color(0xFF2DD4BF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = Color(0xFF38BDF8),
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// =========================================================================
// 2. HOME SCREEN COMPOSABLE (With Hero Section, Metrics, Interactive Diagram, and About Us bio)
// =========================================================================
@Composable
fun HomeScreen(
    meetings: List<ClientMeeting>,
    inquiries: List<ClientInquiry>,
    appointments: List<Appointment>,
    emailLogs: List<EmailLog>,
    autoEmailEnabled: Boolean,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF0D9488)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "VAAN CONSULTING LTD — NEW ZEALAND",
                            color = Color(0xFF2DD4BF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enterprise cloud & data,\nengineered to move.",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Independent IT consultancy led by VAAN consultancy — architecting cloud-native data platforms for banking, energy, and automotive enterprises. Strategy through to production.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTab("bookings") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2DD4BF), contentColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.0f).testTag("home_cta_start")
                        ) {
                            Text("Book Consultation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onNavigateToTab("services") },
                            border = BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.weight(1.0f).testTag("home_cta_capabilities")
                        ) {
                            Text("View Capabilities", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // About Vaan (Screenshot 1 alignment)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "ABOUT VAAN",
                        color = Color(0xFF14B8A6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Architecture that survives contact with production.",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Main body
                    Text(
                        text = "VAAN Consulting is the independent practice of VAAN consultancy, a SAFe 6 Agilist who has spent over sixteen years translating business strategy into cloud-native, data-driven platforms for banking, automotive and energy & utilities organisations.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "The work sits end-to-end: cloud strategy, distributed system design and the non-functional requirements that regulated businesses can't compromise on — security, scalability, performance and resilience. Recent engagements span multi-cloud architecture on AWS, Azure and GCP; data platform modernisation on Snowflake, Databricks and Microsoft Fabric; and event-driven microservices built on Java and Spring Boot.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Working all over New Zealand, VAAN partners with engineering and leadership teams to modernise legacy systems, reduce technical debt and ship measurable outcomes — not just diagrams.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Value pillars (01, 02, 03)
                    listOf(
                        Triple("01", "Strategic ownership", "Complex business requirements translated into scalable, secure, cost-effective architectures."),
                        Triple("02", "Delivery confidence", "Full lifecycle delivery led against business and regulatory standards — not just advisory slides."),
                        Triple("03", "Technical credibility", "Hands-on cloud, data and distributed-systems expertise that validates designs and reduces delivery risk.")
                    ).forEach { (num, title, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = num,
                                color = Color(0xFF14B8A6),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.width(32.dp)
                            )
                            Column {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Capabilities Block (Screenshot 2 alignment)
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(
                    text = "CAPABILITIES",
                    color = Color(0xFF14B8A6),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Four areas where VAAN earns its keep.",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Each engagement draws on the same core stack — adapted to your regulatory context, team maturity and existing cloud footprint.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        // The 4 key areas of capability as beautiful cards
        listOf(
            VaanCapabilityItem(
                Icons.Default.CloudQueue,
                "Multi-cloud architecture",
                "Production-grade, cost-optimised solutions designed across AWS, Azure and GCP — reducing vendor lock-in while maximising the value of each platform.",
                listOf("AWS", "Azure", "GCP", "FinOps")
            ),
            VaanCapabilityItem(
                Icons.Default.Storage,
                "Data platform modernisation",
                "ETL/ELT pipelines and analytics platforms built on Snowflake, Databricks and Microsoft Fabric that turn raw, fragmented data into decisions teams can act on.",
                listOf("Snowflake", "Databricks", "Fabric", "DBT")
            ),
            VaanCapabilityItem(
                Icons.Default.DeveloperMode,
                "Distributed systems & microservices",
                "Event-driven, fault-tolerant systems architected for enterprise-scale workloads, drawing on deep Java and microservices expertise.",
                listOf("Java", "Spring Boot", "Kubernetes", "EDA")
            ),
            VaanCapabilityItem(
                Icons.Default.Verified,
                "Governance & delivery leadership",
                "Cross-functional teams led through SAFe 6 practice — managing technical debt and navigating architecture governance in regulated sectors.",
                listOf("SAFe 6", "Agile", "Risk & Compliance")
            )
        ).forEach { quad ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0D9488)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(quad.icon, quad.title, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = quad.title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = quad.desc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Tag row
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quad.tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(tag, color = Color(0xFF0D9488), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Interactive Technology Hub Diagram
        item {
            InteractiveDiagramCard()
        }

        // Operational Metrics Grid (from website screenshot)
        item {
            Text(
                text = "Track Record & Value",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricItemCard(
                    modifier = Modifier.weight(1f),
                    metric = "16+",
                    label = "Years delivering cloud platforms",
                    color = MaterialTheme.colorScheme.primary
                )
                MetricItemCard(
                    modifier = Modifier.weight(1f),
                    metric = "3",
                    label = "Sectors: banking, energy, auto",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricItemCard(
                    modifier = Modifier.weight(1f),
                    metric = "30%",
                    label = "Typical processing reduction",
                    color = MaterialTheme.colorScheme.tertiary
                )
                MetricItemCard(
                    modifier = Modifier.weight(1f),
                    metric = "28",
                    label = "Professional tech certs held",
                    color = Color(0xFF10B981)
                )
            }
        }

        // Corporate Website Footer (Screenshot 4 footer alignment)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Slate background
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    VaanBrandText(fontSizeSp = 18.sp, isDark = true)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LIMITED • NEW ZEALAND",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Providing enterprise-grade multi-cloud architectural strategy, Snowflake & Databricks integrations, and resilient Java microservices consulting services.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "CONSULTING AREAS",
                        color = Color(0xFF14B8A6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Cloud Migration & Governance",
                        "Data Platform Modernisation",
                        "Distributed Systems & EDA",
                        "Agile Transformation Leadership"
                    ).forEach { area ->
                        Text(
                            text = "• $area",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "CONNECT DIRECTLY",
                        color = Color(0xFF14B8A6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "LinkedIn",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/vaan-consulting/"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open LinkedIn", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        Text(
                            text = "vaanconsulting@gmail.com",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:vaanconsulting@gmail.com"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open email application", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        Text(
                            text = "+64 22 560 1989",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+64225601989"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open dialer", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "© 2026 VAAN Consulting Limited. All rights reserved.",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItemCard(modifier: Modifier = Modifier, metric: String, label: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = metric,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// =========================================================================
// 3. INTERACTIVE NODE DIAGRAM CARD
// =========================================================================
@Composable
fun InteractiveDiagramCard() {
    var selectedNode by remember { mutableStateOf("VAAN") }
    
    val nodeDescriptions = mapOf(
        "VAAN" to "Vaan Consulting is your central integration hub, orchestrating high-performance cloud migration blueprints and low-latency real-time data pipelines.",
        "AWS" to "Deploy scalable EKS clusters, Serverless lambda APIs, secure IAM profiles, and migrate core database mainframes to Postgres RDS.",
        "Azure" to "Enterprise active directory integrations, Azure Synapse warehouses, zero-downtime Azure SQL modernizations, and cloud networking.",
        "GCP" to "High-performance GKE Kubernetes deployment, serverless AppEngine infrastructure, and big-data streaming via Google BigQuery platforms.",
        "Snowflake" to "Migrate massive legacy data pipelines, orchestrate fast bulk loading, and scale analytical databases in secure configurations.",
        "Databricks" to "Deploy unified Delta Lake architectures with real-time streaming pipelines, achieving a verified 30% reduction in processing time.",
        "Fabric" to "Deploy Microsoft Fabric OneLake, organize medallion Bronze-Silver-Gold schemas, and use Direct Lake streaming directly to PowerBI report pages."
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "NodeNetwork")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DashProgress"
    )
    
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enterprise Tech Hub",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tap any node to view our consulting specialties",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val rx = 95.dp.toPx()
                    val ry = 65.dp.toPx()
                    
                    for (i in 0 until 6) {
                        val angle = (i * Math.PI / 3).toFloat()
                        val nx = cx + rx * kotlin.math.cos(angle)
                        val ny = cy + ry * kotlin.math.sin(angle)
                        
                        val nodeName = when(i) {
                            0 -> "GCP"
                            1 -> "Snowflake"
                            2 -> "Databricks"
                            3 -> "Fabric"
                            4 -> "AWS"
                            else -> "Azure"
                        }
                        val isHighlight = selectedNode == "VAAN" || selectedNode == nodeName
                        
                        // Draw connection base lines
                        drawLine(
                            color = if (isHighlight) Color(0xFF334155) else Color(0xFF1E293B),
                            start = Offset(cx, cy),
                            end = Offset(nx, ny),
                            strokeWidth = if (isHighlight) 4f else 2f
                        )
                        
                        // Calculate staggered active progress for each node
                        val staggeredProgress = (animationProgress + (i * 0.15f)) % 1f
                        
                        val dx = nx - cx
                        val dy = ny - cy
                        val len = kotlin.math.hypot(dx, dy)
                        val ux = dx / len
                        val uy = dy / len
                        
                        val dashLen = 20.dp.toPx()
                        val centerDist = staggeredProgress * len
                        val dashStartDist = (centerDist - dashLen / 2).coerceIn(0f, len)
                        val dashEndDist = (centerDist + dashLen / 2).coerceIn(0f, len)
                        
                        val dashStart = Offset(cx + ux * dashStartDist, cy + uy * dashStartDist)
                        val dashEnd = Offset(cx + ux * dashEndDist, cy + uy * dashEndDist)
                        
                        // Alternate colors: Even indices (GCP, Databricks, AWS) get Teal/Cyan; Odd indices (Snowflake, Fabric, Azure) get Gold/Orange
                        val dashColor = if (i % 2 == 0) Color(0xFF14B8A6) else Color(0xFFF59E0B)
                        
                        // Draw glow shadow
                        drawLine(
                            color = dashColor.copy(alpha = 0.35f),
                            start = dashStart,
                            end = dashEnd,
                            strokeWidth = 9f,
                            cap = StrokeCap.Round
                        )
                        
                        // Draw active dash
                        drawLine(
                            color = dashColor,
                            start = dashStart,
                            end = dashEnd,
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                    }
                }
                
                // Central "VAAN" bubble
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .clickable { selectedNode = "VAAN" }
                        .border(
                            width = if (selectedNode == "VAAN") 3.dp else 1.5.dp,
                            color = Color(0xFF14B8A6),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VAAN",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
                
                // Outer nodes mapping
                val outerNodes = listOf("GCP", "Snowflake", "Databricks", "Fabric", "AWS", "Azure")
                outerNodes.forEachIndexed { i, name ->
                    val angle = i * Math.PI / 3
                    val xOffset = (95 * kotlin.math.cos(angle)).dp
                    val yOffset = (65 * kotlin.math.sin(angle)).dp
                    
                    val displayName = when (name) {
                        "Snowflake" -> "SNOW-\nFLAKE"
                        "Databricks" -> "DATA-\nBRICKS"
                        "Azure" -> "AZURE"
                        else -> name.uppercase()
                    }
                    
                    val isSelected = selectedNode == name
                    val accentColor = if (i % 2 == 0) Color(0xFF14B8A6) else Color(0xFFF59E0B)
                    
                    Box(
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else Color(0xFF1E293B),
                                shape = CircleShape
                            )
                            .clickable { selectedNode = name },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 11.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = selectedNode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nodeDescriptions[selectedNode] ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// =========================================================================
// 4. SERVICES SCREEN COMPOSABLE (With Global Search, Persistent Bookmarking, and Detail Overlays)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    bookmarkedServices: Set<String>,
    onToggleBookmark: (String) -> Unit,
    onBookService: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedServiceForDetails by remember { mutableStateOf<VaanService?>(null) }

    val filteredServices = servicesList.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.description.contains(searchQuery, ignoreCase = true) ||
        it.platforms.any { platform -> platform.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Enterprise Capabilities",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Explore services designed to modernize infrastructure and unlock pipeline speed.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search services or platform tags (AWS, Snowflake)...") },
            modifier = Modifier.fillMaxWidth().testTag("services_search_input"),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (filteredServices.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Search, "No matching services", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No services found matching \"$searchQuery\"", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredServices) { service ->
                    val isBookmarked = bookmarkedServices.contains(service.id)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedServiceForDetails = service }
                            .testTag("service_card_${service.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(service.icon, service.title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(service.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(service.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                                    }
                                }
                                IconButton(
                                    onClick = { onToggleBookmark(service.id) },
                                    modifier = Modifier.testTag("bookmark_service_${service.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = service.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                service.platforms.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(tag, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Service Detail Overlay
    selectedServiceForDetails?.let { service ->
        val isBookmarked = bookmarkedServices.contains(service.id)
        Dialog(onDismissRequest = { selectedServiceForDetails = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(service.icon, service.title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(service.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = { onToggleBookmark(service.id) }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(service.subtitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(service.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Technical Scope Areas:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    service.details.forEach { bullet ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(bullet, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { selectedServiceForDetails = null },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dismiss", fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                selectedServiceForDetails = null
                                onBookService(service.title)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Book consultation", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. INSIGHTS (TECH BLOG) SCREEN COMPOSABLE (Offline bookmarked mode, search, category filter, and sharing)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    bookmarkedArticles: Set<String>,
    onToggleBookmark: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedArticleForReading by remember { mutableStateOf<TechArticle?>(null) }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val filteredArticles = articlesList.filter { article ->
        val matchesQuery = article.title.contains(searchQuery, ignoreCase = true) ||
                article.summary.contains(searchQuery, ignoreCase = true) ||
                article.content.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || article.category == selectedCategory
        val matchesBookmarkFilter = !showOnlyBookmarked || bookmarkedArticles.contains(article.id)
        
        matchesQuery && matchesCategory && matchesBookmarkFilter
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Business Insights",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "High-quality cloud architectures and Scaled Agile technical advisories, readable offline.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search technical articles...") },
            modifier = Modifier.fillMaxWidth().testTag("blog_search_input"),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Bookmarked only toggle and categories row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category Filters", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showOnlyBookmarked = !showOnlyBookmarked }
            ) {
                Icon(
                    imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = if (showOnlyBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Saved Only",
                    fontSize = 11.sp,
                    color = if (showOnlyBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        // Scrollable category bar
        val categories = listOf("All", "Data Platform", "Cloud & Digital", "Mobile Dev", "Web Dev", "Bespoke Consulting")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (filteredArticles.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.LibraryBooks,
                        contentDescription = "Empty insights",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (showOnlyBookmarked) "No saved articles yet." else "No insights found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredArticles) { article ->
                    val isSaved = bookmarkedArticles.contains(article.id)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedArticleForReading = article }
                            .testTag("article_card_${article.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(article.category, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(article.readTime, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onToggleBookmark(article.id) },
                                        modifier = Modifier.size(24.dp).testTag("bookmark_article_${article.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(article.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(article.summary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(article.date, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Article Reading Dialog Overlay
    selectedArticleForReading?.let { article ->
        val isSaved = bookmarkedArticles.contains(article.id)
        Dialog(onDismissRequest = { selectedArticleForReading = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(article.category, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(article.readTime, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row {
                            IconButton(onClick = { onToggleBookmark(article.id) }) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Check out this enterprise tech insight from Vaan Consulting: ${article.title}\n\n${article.summary}\n\nRead more at https://www.vaanconsulting.com/insights")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }
                            ) {
                                Icon(Icons.Default.Share, "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(article.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Published on ${article.date} by Vaan Systems Advisory", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Full Article Content
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = article.content,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedArticleForReading = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close article")
                    }
                }
            }
        }
    }
}

// =========================================================================
// 6. VAANAI CHAT SCREEN COMPOSABLE (With Chat Bubbles, Loading and Quick Suggestion Chips)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaanAiChatScreen(viewModel: AppViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var userMessageText by remember { mutableStateOf("") }

    val chatListState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Scroll to bottom when new messages are added
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VaanAI Assistant",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ask questions regarding our multi-cloud, database, and agilest services.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { viewModel.clearChatHistory() },
                modifier = Modifier.testTag("chat_clear_btn")
            ) {
                Icon(Icons.Default.DeleteSweep, "Clear Chat", tint = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Chat Bubble list
        LazyColumn(
            state = chatListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Suggestion chips at the top of empty/fresh chats
            if (chatMessages.size <= 1) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Suggested topics to click:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        val chips = listOf(
                            "Tell me about Databricks pipeline optimizations",
                            "How does VAAN consultancy support SAFe 6 transformations?",
                            "Do you perform legacy mainframe DB2 database migrations?",
                            "Can you build secure, offline-first mobile applications?"
                        )
                        chips.forEach { prompt ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { viewModel.sendChatMessage(prompt) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HelpOutline, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(prompt, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            items(chatMessages) { msg ->
                val isUser = msg.sender == "user"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                fontSize = 12.sp,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            if (isChatLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.widthIn(max = 120.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("VaanAI is writing...", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userMessageText,
                onValueChange = { userMessageText = it },
                placeholder = { Text("Ask about our architectures...") },
                modifier = Modifier.weight(1f).testTag("chat_text_input"),
                singleLine = true,
                trailingIcon = {
                    if (userMessageText.isNotEmpty()) {
                        IconButton(onClick = { userMessageText = "" }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                }
            )
            IconButton(
                onClick = {
                    if (userMessageText.trim().isNotEmpty()) {
                        viewModel.sendChatMessage(userMessageText)
                        userMessageText = ""
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("chat_send_btn")
            ) {
                Icon(Icons.Default.Send, "Send", tint = Color.White)
            }
        }
    }
}

// =========================================================================
// 7. PORTAL & BOOKINGS COMPOSABLE (Unified Client Form + Dynamic Maps/Dial Intents + Developer outbox logs)
// =========================================================================
@Composable
fun PortalScreen(
    meetings: List<ClientMeeting>,
    inquiries: List<ClientInquiry>,
    appointments: List<Appointment>,
    emailLogs: List<EmailLog>,
    autoEmailEnabled: Boolean,
    onToggleAutoEmail: (Boolean) -> Unit,
    onNavigateToTab: (String) -> Unit,
    selectedServiceForBooking: String,
    onClearBookingSelection: () -> Unit,
    viewModel: AppViewModel
) {
    var subTab by remember { mutableStateOf("book") } // "book", "contact"

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = when(subTab) {
                "book" -> 0
                "contact" -> 1
                else -> 0
            },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Tab(selected = subTab == "book", onClick = { subTab = "book" }) {
                Text("Book Call", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = subTab == "contact", onClick = { subTab = "contact" }) {
                Text("Contact Us", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (subTab) {
                "book" -> {
                    ConsultationBookingForm(
                        selectedService = selectedServiceForBooking,
                        onClearSelection = onClearBookingSelection,
                        onBook = { name, email, service, time, duration, notes ->
                            viewModel.createAppointment(name, email, service, time, duration, notes)
                        }
                    )
                }
                "contact" -> {
                    ContactUsView(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsView(viewModel: AppViewModel) {
    val context = LocalContext.current
    
    // Form States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf("Data Platform Architecture & Strategy") }
    var messageDescription by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    
    // Validation States
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var messageError by remember { mutableStateOf<String?>(null) }

    fun isValidEmail(emailStr: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return emailStr.matches(emailRegex)
    }
    
    val serviceOptions = listOf(
        "Data Platform Architecture & Strategy",
        "Multi-Cloud Security & Performance",
        "Distributed Systems & Microservices",
        "Scaled Agile Coaching & Governance"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero / Introduction (Screenshot 4 layout)
        item {
            Text(
                text = "Contact Us Directly",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Feel free to reach out via email or phone for immediate consulting inquiries. Based in New Zealand, available for both localized NZ/AU engagements and remote global advisory roles.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        // Contact details card (Screenshot 4 block)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CONTACT DETAILS",
                        color = Color(0xFF14B8A6),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // EMAIL ADDRESS
                    Column(modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:vaanconsulting@gmail.com"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Could not open email application", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("EMAIL ADDRESS", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("vaanconsulting@gmail.com", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // PHONE CONTACT
                    Column(modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+64225601989"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Could not open dialer", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("PHONE CONTACT", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("+64 22 560 1989", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // CONSULTING OFFICE
                    Column {
                        Text("CONSULTING OFFICE", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("New Zealand", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "VAAN Consulting Limited • New Zealand",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Send a Message Card Form (Screenshot 4 block)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("contact_message_form_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "SEND A MESSAGE",
                        color = Color(0xFF14B8A6),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSubmitted) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.1f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CheckCircle, "Success", tint = Color(0xFF10B981), modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Inquiry Submitted!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF10B981))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Your consulting inquiry has been received. Our principal architect will compile an automated strategy draft and send it to your inbox shortly.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                TextButton(onClick = {
                                    isSubmitted = false
                                    name = ""
                                    email = ""
                                    company = ""
                                    messageDescription = ""
                                }) {
                                    Text("Send Another Message", fontWeight = FontWeight.Bold, color = Color(0xFF14B8A6))
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // YOUR NAME *
                            Column {
                                Text("YOUR NAME *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { 
                                        name = it 
                                        if (it.isNotBlank()) nameError = null
                                    },
                                    isError = nameError != null,
                                    supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                                    modifier = Modifier.fillMaxWidth().testTag("inquiry_name"),
                                    placeholder = { Text("e.g. John Doe", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            // EMAIL ADDRESS *
                            Column {
                                Text("EMAIL ADDRESS *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { 
                                        email = it 
                                        if (it.isNotBlank() && isValidEmail(it)) emailError = null
                                    },
                                    isError = emailError != null,
                                    supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                                    modifier = Modifier.fillMaxWidth().testTag("inquiry_email"),
                                    placeholder = { Text("e.g. john@company.com", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            // COMPANY / ORG
                            Column {
                                Text("COMPANY / ORG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = company,
                                    onValueChange = { company = it },
                                    modifier = Modifier.fillMaxWidth().testTag("inquiry_company"),
                                    placeholder = { Text("e.g. Acme Corp", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            // CONSULTING SERVICE REQUIRED
                            Column {
                                Text("CONSULTING SERVICE REQUIRED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { dropdownExpanded = !dropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedService,
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("inquiry_service"),
                                        shape = RoundedCornerShape(10.dp),
                                        trailingIcon = {
                                            Icon(Icons.Default.ArrowDropDown, "Select Service")
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = dropdownExpanded,
                                        onDismissRequest = { dropdownExpanded = false }
                                    ) {
                                        serviceOptions.forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option, fontSize = 12.sp) },
                                                onClick = {
                                                    selectedService = option
                                                    dropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // MESSAGE DESCRIPTION *
                            Column {
                                Text("MESSAGE DESCRIPTION *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = messageDescription,
                                    onValueChange = { 
                                        messageDescription = it 
                                        if (it.isNotBlank()) messageError = null
                                    },
                                    isError = messageError != null,
                                    supportingText = messageError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("inquiry_message"),
                                    placeholder = { Text("Outline your technical bottlenecks, platform goals, or timeline boundaries...", fontSize = 13.sp) },
                                    maxLines = 5,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    var hasError = false
                                    
                                    if (name.isBlank()) {
                                        nameError = "Name is required"
                                        hasError = true
                                    } else {
                                        nameError = null
                                    }

                                    if (email.isBlank()) {
                                        emailError = "Corporate email is required"
                                        hasError = true
                                    } else if (!isValidEmail(email)) {
                                        emailError = "Invalid email format"
                                        hasError = true
                                    } else {
                                        emailError = null
                                    }

                                    if (messageDescription.isBlank()) {
                                        messageError = "Message is required"
                                        hasError = true
                                    } else {
                                        messageError = null
                                    }

                                    if (!hasError) {
                                        viewModel.submitInquiry(
                                            clientName = name,
                                            clientEmail = email,
                                            companyName = company.ifBlank { "Independent" },
                                            subject = selectedService,
                                            message = messageDescription
                                        )
                                        isSubmitted = true
                                        android.widget.Toast.makeText(context, "Inquiry dispatched successfully!", android.widget.Toast.LENGTH_LONG).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "Please complete all required fields correctly", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("inquiry_submit_button")
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Send, "Send", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Submit Inquiry", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // WhatsApp Direct Block
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/64225601989"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Could not open WhatsApp link", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }.testTag("contact_whatsapp")
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Chat, "WhatsApp", tint = Color(0xFF10B981))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Direct WhatsApp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Chat instantly with VAAN consultancy on +64 22 560 1989", fontSize = 12.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultationBookingForm(
    selectedService: String,
    onClearSelection: () -> Unit,
    onBook: (String, String, String, Long, Int, String) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf(if (selectedService.isNotEmpty()) selectedService else "Data Platform Architecture & Strategy") }
    var proposedDate by remember { mutableStateOf("") }
    var proposedTime by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("30") }
    var notes by remember { mutableStateOf("") }
    var bookingConfirmed by remember { mutableStateOf(false) }

    // Validation error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }

    fun isValidEmail(emailStr: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return emailStr.matches(emailRegex)
    }

    // Set up native DatePickerDialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
            proposedDate = sdf.format(cal.time)
            dateError = null // Clear error when selected
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Set up native TimePickerDialog
    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat("hh:mm a", Locale.US)
            proposedTime = sdf.format(cal.time)
            timeError = null // Clear error when selected
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false // 12-hour format with AM/PM
    )

    LaunchedEffect(selectedService) {
        if (selectedService.isNotEmpty()) {
            serviceType = selectedService
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (bookingConfirmed) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, "Success", tint = Color(0xFF10B981), modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Discovery Call Booked!", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF10B981))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("We have scheduled your discovery call regarding $serviceType. An automated SMTP confirmation has been logged with your calendar access details.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                bookingConfirmed = false
                                name = ""
                                email = ""
                                notes = ""
                                proposedDate = ""
                                proposedTime = ""
                                onClearSelection()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Book Another Session")
                        }
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "Request a Discovery Session",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Fill out this brief to reserve a technical alignment discovery slot on our local calendar database.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it 
                        if (it.isNotBlank()) nameError = null
                    },
                    label = { Text("Your Name *") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth().testTag("booking_name_input"),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it 
                        if (it.isNotBlank() && isValidEmail(it)) emailError = null
                    },
                    label = { Text("Corporate Email Address *") },
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth().testTag("booking_email_input"),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, null) }
                )
            }

            item {
                Column {
                    Text("Select Consulting Domain", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    val categories = listOf(
                        "Data Platform Architecture & Strategy",
                        "Cloud & Digital Transformation",
                        "Mobile Application Development",
                        "Web Application Development",
                        "Bespoke Technology Consulting / Other"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = serviceType == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { serviceType = cat }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(cat, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).clickable { datePickerDialog.show() }) {
                        OutlinedTextField(
                            value = proposedDate,
                            onValueChange = {},
                            label = { Text("Date *") },
                            readOnly = true,
                            enabled = false,
                            isError = dateError != null,
                            supportingText = dateError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledBorderColor = if (dateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("booking_date_input"),
                            leadingIcon = { Icon(Icons.Default.CalendarToday, null) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f).clickable { timePickerDialog.show() }) {
                        OutlinedTextField(
                            value = proposedTime,
                            onValueChange = {},
                            label = { Text("Time *") },
                            readOnly = true,
                            enabled = false,
                            isError = timeError != null,
                            supportingText = timeError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledBorderColor = if (timeError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("booking_time_input"),
                            leadingIcon = { Icon(Icons.Default.AccessTime, null) }
                        )
                    }
                }
            }

            item {
                Column {
                    Text("Session Duration", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf("15", "30", "45", "60").forEach { mins ->
                            val isSelected = durationMinutes == mins
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { durationMinutes = mins }
                            ) {
                                RadioButton(selected = isSelected, onClick = { durationMinutes = mins })
                                Text("$mins min", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Describe your technical stack or goals") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("booking_notes_input"),
                    maxLines = 4,
                    leadingIcon = { Icon(Icons.Default.Notes, null) }
                )
            }

            item {
                Button(
                    onClick = {
                        var hasError = false
                        
                        if (name.isBlank()) {
                            nameError = "Name is required"
                            hasError = true
                        } else {
                            nameError = null
                        }

                        if (email.isBlank()) {
                            emailError = "Corporate email is required"
                            hasError = true
                        } else if (!isValidEmail(email)) {
                            emailError = "Invalid email format"
                            hasError = true
                        } else {
                            emailError = null
                        }

                        if (proposedDate.isBlank()) {
                            dateError = "Date required"
                            hasError = true
                        } else {
                            dateError = null
                        }

                        if (proposedTime.isBlank()) {
                            timeError = "Time required"
                            hasError = true
                        } else {
                            timeError = null
                        }

                        if (!hasError) {
                            val timeMs = System.currentTimeMillis() + (24 * 3600 * 1000L)
                            onBook(name, email, serviceType, timeMs, durationMinutes.toIntOrNull() ?: 30, "Date: $proposedDate, Time: $proposedTime. Notes: $notes")
                            bookingConfirmed = true
                        } else {
                            android.widget.Toast.makeText(context, "Please fill out all mandatory fields correctly", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_booking_btn")
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Register Session", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = count,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ==========================================
// 2. SCHEDULES / MEETINGS SCREEN
// ==========================================
@Composable
fun SchedulesScreen(
    meetings: List<ClientMeeting>,
    onAddMeeting: (String, String, String, String, Long, String) -> Unit,
    onCancelMeeting: (ClientMeeting) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf("All") }

    val filteredMeetings = when (filterStatus) {
        "Scheduled" -> meetings.filter { it.status == "Scheduled" }
        "Cancelled" -> meetings.filter { it.status == "Cancelled" }
        else -> meetings
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Client Meetings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                // Filter chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("All", "Scheduled", "Cancelled").forEach { status ->
                        val isSelected = filterStatus == status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable { filterStatus = status }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredMeetings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No meetings match this filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMeetings) { meeting ->
                        MeetingCardItem(meeting = meeting, onCancel = { onCancelMeeting(meeting) })
                    }
                }
            }
        }

        // Floating Action Button to create a meeting
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("schedule_meeting_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Meeting")
        }

        if (showDialog) {
            AddMeetingDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, email, title, desc, time, link ->
                    onAddMeeting(name, email, title, desc, time, link)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun MeetingCardItem(meeting: ClientMeeting, onCancel: () -> Unit) {
    val df = SimpleDateFormat("EEEE, MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val formattedDate = df.format(Date(meeting.dateTime))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = meeting.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Client: ${meeting.clientName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (meeting.status == "Scheduled") Color(0xFFE0F2FE)
                            else Color(0xFFF1F5F9)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = meeting.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (meeting.status == "Scheduled") Color(0xFF0369A1) else Color(0xFF475569)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Logistics info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = meeting.meetingLink,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (meeting.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = meeting.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Automated notification tag indicator
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (meeting.notificationSent) Icons.Default.CheckCircle else Icons.Default.Sync,
                        contentDescription = null,
                        tint = if (meeting.notificationSent) Color(0xFF10B981) else Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (meeting.notificationSent) "Automated Email Sent" else "Email Sync Queued",
                        fontSize = 11.sp,
                        color = if (meeting.notificationSent) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (meeting.status == "Scheduled") {
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddMeetingDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, Long, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var meetLink by remember { mutableStateOf("https://meet.google.com/vaa-ncon-slt") }

    // Manual date inputs for absolute reliability
    var monthStr by remember { mutableStateOf("07") }
    var dayStr by remember { mutableStateOf("15") }
    var yearStr by remember { mutableStateOf("2026") }
    var hourStr by remember { mutableStateOf("10") }
    var minuteStr by remember { mutableStateOf("30") }
    var amPm by remember { mutableStateOf("AM") }

    var errorText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Schedule New Meeting",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Configures Client calendar & automatically triggers confirmation SMTP notifications.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Client Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_meeting_name"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Client Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_meeting_email"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Meeting Title (e.g. Discovery)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_meeting_title"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Agenda / Brief Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = meetLink,
                        onValueChange = { meetLink = it },
                        label = { Text("Meeting Link") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Date/Time manual entry form
                item {
                    Text(
                        text = "Date & Time Configuration (Tomorrow)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = monthStr,
                            onValueChange = { if (it.length <= 2) monthStr = it },
                            label = { Text("MM") },
                            modifier = Modifier.weight(1.0f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = dayStr,
                            onValueChange = { if (it.length <= 2) dayStr = it },
                            label = { Text("DD") },
                            modifier = Modifier.weight(1.0f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = yearStr,
                            onValueChange = { if (it.length <= 4) yearStr = it },
                            label = { Text("YYYY") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = hourStr,
                            onValueChange = { if (it.length <= 2) hourStr = it },
                            label = { Text("Hour") },
                            modifier = Modifier.weight(1.0f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = minuteStr,
                            onValueChange = { if (it.length <= 2) minuteStr = it },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1.0f),
                            singleLine = true
                        )
                        // AM/PM select toggle
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                                .clickable { amPm = if (amPm == "AM") "PM" else "AM" }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = amPm,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                if (errorText.isNotEmpty()) {
                    item {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || email.isBlank() || title.isBlank()) {
                                    errorText = "Please fill in all primary fields."
                                    return@Button
                                }
                                try {
                                    val calendar = Calendar.getInstance()
                                    val year = yearStr.toInt()
                                    val month = monthStr.toInt() - 1
                                    val day = dayStr.toInt()
                                    var hour = hourStr.toInt()
                                    val minute = minuteStr.toInt()

                                    if (amPm == "PM" && hour < 12) hour += 12
                                    if (amPm == "AM" && hour == 12) hour = 0

                                    calendar.set(year, month, day, hour, minute, 0)
                                    val timestamp = calendar.timeInMillis

                                    onConfirm(name, email, title, desc, timestamp, meetLink)
                                } catch (e: Exception) {
                                    errorText = "Invalid Date/Time configuration values."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("submit_meeting_button")
                        ) {
                            Text("Schedule")
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 3. INQUIRIES SCREEN
// ==========================================
@Composable
fun InquiriesScreen(
    inquiries: List<ClientInquiry>,
    viewModel: AppViewModel,
    onSubmitReply: (ClientInquiry, String) -> Unit,
    onCreateInquiry: (String, String, String, String, String) -> Unit
) {
    var selectedInquiry by remember { mutableStateOf<ClientInquiry?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Website Inquiries",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                // Simulated Submit from website
                TextButton(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Inquiry", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (inquiries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No inquiries synchronized from Vaan website.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(inquiries) { inquiry ->
                        InquiryCardItem(
                            inquiry = inquiry,
                            onClick = { selectedInquiry = inquiry }
                        )
                    }
                }
            }
        }

        // Details dialog with AI drafting inside
        if (selectedInquiry != null) {
            InquiryDetailsDialog(
                inquiry = selectedInquiry!!,
                viewModel = viewModel,
                onDismiss = {
                    selectedInquiry = null
                    viewModel.resetAiState()
                },
                onSendReply = { reply ->
                    onSubmitReply(selectedInquiry!!, reply)
                    selectedInquiry = null
                    viewModel.resetAiState()
                }
            )
        }

        // Simulation dialog
        if (showCreateDialog) {
            SimulateInquiryDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, email, company, subject, msg ->
                    onCreateInquiry(name, email, company, subject, msg)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun InquiryCardItem(inquiry: ClientInquiry, onClick: () -> Unit) {
    val df = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val receivedDate = df.format(Date(inquiry.receivedTime))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inquiry.companyName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (inquiry.status == "New") Color(0xFFFEF3C7)
                            else Color(0xFFD1FAE5)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = inquiry.status.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (inquiry.status == "New") Color(0xFFD97706) else Color(0xFF059669)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = inquiry.subject,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = inquiry.message,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "From: ${inquiry.clientName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = receivedDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InquiryDetailsDialog(
    inquiry: ClientInquiry,
    viewModel: AppViewModel,
    onDismiss: () -> Unit,
    onSendReply: (String) -> Unit
) {
    val aiState by viewModel.aiDraftState.collectAsState()
    var replyText by remember { mutableStateOf("") }

    // Synchronize drafted text into the input field when AI responds
    LaunchedEffect(aiState) {
        if (aiState is AiDraftState.Success) {
            replyText = (aiState as AiDraftState.Success).draft
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Inquiry Detail",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "CLIENT BRIEF",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Name: ${inquiry.clientName} (${inquiry.clientEmail})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Company: ${inquiry.companyName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subject: ${inquiry.subject}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = inquiry.message,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // AI Assist Action Block
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Vaan AI Draft Assistant",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                if (aiState !is AiDraftState.Loading) {
                                    TextButton(
                                        onClick = { viewModel.generateAIEmailDraftForInquiry(inquiry) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                    ) {
                                        Text("Draft Response", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            when (aiState) {
                                is AiDraftState.Loading -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Consulting Gemini models to formulate professional reply...",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                is AiDraftState.Success -> {
                                    Text(
                                        text = "AI Draft created! Modify the email body below to your liking.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "Generate an optimized, professional consulting response addressing Kubernetes, Cloud Architecture, or Mobile development automatically.",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Reply Input
                item {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Email Reply Body") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("reply_input_field"),
                        maxLines = 8
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onSendReply(replyText)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("send_reply_button")
                        ) {
                            Text("Send SMTP Notification")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimulateInquiryDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("Elena") }
    var email by remember { mutableStateOf("elena.r@innovatefintech.io") }
    var company by remember { mutableStateOf("Innovate Fintech") }
    var subject by remember { mutableStateOf("Kubernetes Clustering") }
    var msg by remember { mutableStateOf("We need cloud engineers to review and scale our AWS EKS systems.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Simulate Website Submission",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = msg, onValueChange = { msg = it }, label = { Text("Inquiry Message") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(name, email, company, subject, msg) }) { Text("Submit") }
                }
            }
        }
    }
}


// ==========================================
// 4. APPOINTMENTS SCREEN
// ==========================================
@Composable
fun AppointmentsScreen(
    appointments: List<Appointment>,
    viewModel: AppViewModel,
    onAddAppointment: (String, String, String, Long, Int, String) -> Unit,
    onConfirmAppointment: (Appointment) -> Unit,
    onCompleteAppointment: (Appointment) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Consulting Bookings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (appointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No consulting bookings registered.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(appointments) { appt ->
                        AppointmentCardItem(
                            appt = appt,
                            onConfirm = { onConfirmAppointment(appt) },
                            onComplete = { onCompleteAppointment(appt) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Appointment")
        }

        if (showDialog) {
            AddAppointmentDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, email, service, time, duration, notes ->
                    onAddAppointment(name, email, service, time, duration, notes)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AppointmentCardItem(appt: Appointment, onConfirm: () -> Unit, onComplete: () -> Unit) {
    val df = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val dateText = df.format(Date(appt.dateTime))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = appt.serviceType,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Duration: ${appt.durationMinutes} Minutes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Badge for appointment status
                val badgeBg = when (appt.status) {
                    "Pending" -> Color(0xFFFEF3C7)
                    "Confirmed" -> Color(0xFFD1FAE5)
                    else -> Color(0xFFF1F5F9)
                }
                val badgeColor = when (appt.status) {
                    "Pending" -> Color(0xFFD97706)
                    "Confirmed" -> Color(0xFF059669)
                    else -> Color(0xFF475569)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = appt.status.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Client: ${appt.clientName} (${appt.clientEmail})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = dateText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (appt.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${appt.notes}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick actions
            if (appt.status == "Pending" || appt.status == "Confirmed") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (appt.status == "Pending") {
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Confirm Booking", fontSize = 12.sp)
                        }
                    } else if (appt.status == "Confirmed") {
                        TextButton(
                            onClick = onComplete,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF10B981))
                        ) {
                            Text("Mark Completed", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddAppointmentDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, Long, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf("Data Platform Architecture & Strategy") }
    var durationMinutes by remember { mutableStateOf("30") }
    var notes by remember { mutableStateOf("") }

    // Date/Time manual entries
    var monthStr by remember { mutableStateOf("07") }
    var dayStr by remember { mutableStateOf("16") }
    var yearStr by remember { mutableStateOf("2026") }
    var hourStr by remember { mutableStateOf("02") }
    var minuteStr by remember { mutableStateOf("00") }
    var amPm by remember { mutableStateOf("PM") }

    var errorText by remember { mutableStateOf("") }

    val services = listOf(
        "Data Platform Architecture & Strategy",
        "Cloud & Digital Transformation",
        "Mobile Application Development",
        "Web Application Development",
        "Bespoke Technology Consulting / Other"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "New Consultation Booking",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Client Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }

                item {
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Client Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }

                // Service Category Selection list
                item {
                    Text(text = "Consulting Service Category", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        services.take(2).forEach { s ->
                            val isSelected = selectedService == s
                            Box(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { selectedService = s }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = s.split(" ").first(), color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        services.takeLast(2).forEach { s ->
                            val isSelected = selectedService == s
                            Box(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { selectedService = s }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = s.split(" ").first(), color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = durationMinutes,
                        onValueChange = { durationMinutes = it },
                        label = { Text("Duration (Minutes)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Requirements Brief / Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }

                item {
                    Text(
                        text = "Preferred Date Configuration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(value = monthStr, onValueChange = { if (it.length <= 2) monthStr = it }, label = { Text("MM") }, modifier = Modifier.weight(1.0f), singleLine = true)
                        OutlinedTextField(value = dayStr, onValueChange = { if (it.length <= 2) dayStr = it }, label = { Text("DD") }, modifier = Modifier.weight(1.0f), singleLine = true)
                        OutlinedTextField(value = yearStr, onValueChange = { if (it.length <= 4) yearStr = it }, label = { Text("YYYY") }, modifier = Modifier.weight(1.5f), singleLine = true)
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(value = hourStr, onValueChange = { if (it.length <= 2) hourStr = it }, label = { Text("Hour") }, modifier = Modifier.weight(1.0f), singleLine = true)
                        OutlinedTextField(value = minuteStr, onValueChange = { if (it.length <= 2) minuteStr = it }, label = { Text("Min") }, modifier = Modifier.weight(1.0f), singleLine = true)
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                                .clickable { amPm = if (amPm == "AM") "PM" else "AM" }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = amPm, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                        }
                    }
                }

                if (errorText.isNotEmpty()) {
                    item {
                        Text(text = errorText, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || email.isBlank()) {
                                    errorText = "Please fill in all client identifiers."
                                    return@Button
                                }
                                try {
                                    val calendar = Calendar.getInstance()
                                    val year = yearStr.toInt()
                                    val month = monthStr.toInt() - 1
                                    val day = dayStr.toInt()
                                    var hour = hourStr.toInt()
                                    val minute = minuteStr.toInt()
                                    val duration = durationMinutes.toIntOrNull() ?: 30

                                    if (amPm == "PM" && hour < 12) hour += 12
                                    if (amPm == "AM" && hour == 12) hour = 0

                                    calendar.set(year, month, day, hour, minute, 0)
                                    onConfirm(name, email, selectedService, calendar.timeInMillis, duration, notes)
                                } catch (e: Exception) {
                                    errorText = "Invalid scheduling values entered."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Book Slot")
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 5. AUTOMATED OUTBOX SCREEN
// ==========================================
@Composable
fun OutboxScreen(
    emailLogs: List<EmailLog>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SMTP Notification Dispatch",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "A complete log of real-time automated emails dispatched to clients.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (emailLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Outbox logs are currently empty.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(emailLogs) { log ->
                    OutboxLogItem(log = log)
                }
            }
        }
    }
}

@Composable
fun OutboxLogItem(log: EmailLog) {
    val df = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val formattedTime = df.format(Date(log.sentTime))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.triggerEvent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = formattedTime,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.subject,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Recipient: ${log.recipient}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.body,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Delivered via Vaan SMTP Secure Client Gateway",
                    fontSize = 10.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

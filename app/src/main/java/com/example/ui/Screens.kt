package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.focus.onFocusChanged
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Appointment
import com.example.data.ClientInquiry
import com.example.data.ClientMeeting
import com.example.data.EmailLog
import com.example.viewmodel.AiDraftState
import com.example.viewmodel.AppViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
    val details: List<String>,
    val accentColor: Color = Color(0xFF38BDF8)
)

data class PillarObjective(
    val title: String,
    val desc: String
)

data class PillarPhase(
    val phase: String,
    val title: String,
    val desc: String
)

data class PillarItem(
    val num: String,
    val title: String,
    val desc: String,
    val accentColor: Color,
    val overview: String = "",
    val objectives: List<PillarObjective> = emptyList(),
    val roadmap: List<PillarPhase> = emptyList(),
    val deliverables: List<String> = emptyList()
)

data class CapabilitySolution(
    val title: String,
    val desc: String
)

data class VaanCapabilityItem(
    val icon: ImageVector,
    val title: String,
    val desc: String,
    val tags: List<String>,
    val accentColor: Color = Color(0xFF14B8A6),
    val overview: String = "",
    val solutions: List<CapabilitySolution> = emptyList(),
    val techStackDetails: Map<String, String> = emptyMap(),
    val strategicOutcomes: List<String> = emptyList()
)

@Composable
fun InteractiveValuePillarRow(
    pillar: PillarItem,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = isHovered || isPressed

    val animatedGlowAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.22f else 0.08f,
        animationSpec = tween(durationMillis = 250),
        label = "pillarGlow"
    )
    val animatedBorderAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.85f else 0.45f,
        animationSpec = tween(durationMillis = 250),
        label = "pillarBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .drawBehind {
                // Soft outer border glow with matching pillar color
                drawRoundRect(
                    color = pillar.accentColor.copy(alpha = if (isActive) 0.28f else 0.14f),
                    topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx(), 14.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        pillar.accentColor.copy(alpha = animatedGlowAlpha),
                        Color.Transparent
                    )
                )
            )
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        pillar.accentColor.copy(alpha = animatedBorderAlpha),
                        pillar.accentColor.copy(alpha = animatedBorderAlpha * 0.35f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .hoverable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = pillar.num,
                color = pillar.accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.width(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pillar.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = pillar.accentColor,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(pillar.accentColor.copy(alpha = if (isActive) 0.25f else 0.12f))
                            .border(1.dp, pillar.accentColor.copy(alpha = 0.35f), CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open subpage",
                            tint = pillar.accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pillar.desc,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun InteractiveCapabilityCard(
    quad: VaanCapabilityItem,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = isHovered || isPressed

    val accent = quad.accentColor

    val animatedGlowAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.25f else 0.08f,
        animationSpec = tween(durationMillis = 250),
        label = "cardGlow"
    )
    val animatedBorderAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.90f else 0.50f,
        animationSpec = tween(durationMillis = 250),
        label = "cardBorderAlpha"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Soft outer border backlight glow with matching accent color
                drawRoundRect(
                    color = accent.copy(alpha = if (isActive) 0.32f else 0.16f),
                    topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx(), 22.dp.toPx())
                )
                // Radial backlight ambient glow behind card
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = if (isActive) 0.40f else 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.2f),
                        radius = size.width * 0.85f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx(), 20.dp.toPx())
                )
            }
            .border(
                width = if (isActive) 2.dp else 1.25.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = animatedBorderAlpha),
                        accent.copy(alpha = animatedBorderAlpha * 0.35f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .hoverable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accent.copy(alpha = animatedGlowAlpha),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) accent else accent.copy(alpha = 0.18f))
                            .border(
                                width = 1.dp,
                                color = accent.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = quad.icon,
                            contentDescription = quad.title,
                            tint = if (isActive) Color.Black else accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = quad.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = accent,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accent.copy(alpha = if (isActive) 0.25f else 0.12f))
                            .border(1.dp, accent.copy(alpha = 0.35f), CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View subpage",
                            tint = accent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quad.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accent.copy(alpha = if (isActive) 0.22f else 0.08f))
                                .border(
                                    width = 1.dp,
                                    color = accent.copy(alpha = if (isActive) 0.5f else 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                color = accent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// PILLAR SUBPAGE DETAIL DIALOG
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillarDetailSubpageDialog(
    pillar: PillarItem,
    onDismiss: () -> Unit,
    onBookConsultation: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.25.dp, pillar.accentColor.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Navigation Bar Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(pillar.accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PILLAR ${pillar.num}",
                                    color = pillar.accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pillar.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close subpage",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, pillar.accentColor.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                pillar.accentColor.copy(alpha = 0.22f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Text(
                                        text = pillar.num,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = pillar.accentColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pillar.title,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = pillar.desc,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }

                    // Executive Summary
                    if (pillar.overview.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    text = "EXECUTIVE SUMMARY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = pillar.accentColor,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = pillar.overview,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }

                    // Objectives
                    if (pillar.objectives.isNotEmpty()) {
                        item {
                            Text(
                                text = "CORE OBJECTIVES & STRATEGY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = pillar.accentColor,
                                letterSpacing = 1.2.sp
                            )
                        }

                        items(pillar.objectives) { obj ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = pillar.accentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = obj.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = obj.desc,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Execution Roadmap
                    if (pillar.roadmap.isNotEmpty()) {
                        item {
                            Text(
                                text = "EXECUTION ROADMAP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = pillar.accentColor,
                                letterSpacing = 1.2.sp
                            )
                        }

                        items(pillar.roadmap) { step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(pillar.accentColor)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = step.phase,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = step.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = step.desc,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Deliverables
                    if (pillar.deliverables.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, pillar.accentColor.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "KEY DELIVERABLES",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = pillar.accentColor,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    pillar.deliverables.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = pillar.accentColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = item,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Close Button
                    item {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = pillar.accentColor),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Close details",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// CAPABILITY SUBPAGE DETAIL DIALOG
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapabilityDetailSubpageDialog(
    capability: VaanCapabilityItem,
    onDismiss: () -> Unit,
    onBookConsultation: (String) -> Unit
) {
    val accent = capability.accentColor

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.25.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Navigation Bar Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = capability.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close subpage",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                accent.copy(alpha = 0.22f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(accent)
                                                .padding(10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = capability.icon,
                                                contentDescription = capability.title,
                                                tint = Color.Black,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = capability.title,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = capability.desc,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 19.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    // Tags
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        capability.tags.forEach { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(accent.copy(alpha = 0.18f))
                                                    .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = tag,
                                                    color = accent,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Executive Overview
                    if (capability.overview.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    text = "CAPABILITY OVERVIEW",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accent,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = capability.overview,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }

                    // Modern Solutions
                    if (capability.solutions.isNotEmpty()) {
                        item {
                            Text(
                                text = "MODERN ARCHITECTURE SOLUTIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accent,
                                letterSpacing = 1.2.sp
                            )
                        }

                        items(capability.solutions) { sol ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(accent)
                                            .padding(2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = sol.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = sol.desc,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Tech Stack Details
                    if (capability.techStackDetails.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "TECHNOLOGY STACK ECOSYSTEM",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = accent,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    capability.techStackDetails.forEach { (cat, stack) ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "$cat: ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = accent,
                                                modifier = Modifier.width(100.dp)
                                            )
                                            Text(
                                                text = stack,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Strategic Outcomes
                    if (capability.strategicOutcomes.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    text = "MEASURABLE ENTERPRISE OUTCOMES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accent,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                capability.strategicOutcomes.forEach { outcome ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = outcome,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Close Button
                    item {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Close details",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class MetricDetailItem(
    val metric: String,
    val label: String,
    val color: Color,
    val title: String,
    val subtitle: String,
    val overview: String,
    val keyPoints: List<Pair<String, String>>,
    val outcomes: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricDetailSubpageDialog(
    metricItem: MetricDetailItem,
    onDismiss: () -> Unit,
    onBookConsultation: (String) -> Unit
) {
    val accent = metricItem.color

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = Color(0xFF0B132B)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(accent.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accent.copy(alpha = 0.2f))
                                    .border(1.dp, accent, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = metricItem.metric,
                                    color = accent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = metricItem.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 19.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "TRACK RECORD & PERFORMANCE METRIC",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accent,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                HorizontalDivider(color = accent.copy(alpha = 0.2f))

                // Content List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Subtitle & Overview
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = metricItem.subtitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accent,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = metricItem.overview,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }

                    // Key Breakdown Points
                    if (metricItem.keyPoints.isNotEmpty()) {
                        item {
                            Text(
                                text = "CORE DELIVERABLES & DOMAIN METRICS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accent,
                                letterSpacing = 1.2.sp
                            )
                        }

                        items(metricItem.keyPoints) { (headline, detail) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(accent)
                                            .padding(2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = headline,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = detail,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Outcomes
                    if (metricItem.outcomes.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "BUSINESS & ARCHITECTURAL IMPACT",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = accent,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    metricItem.outcomes.forEach { outcome ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = accent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = outcome,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Close Button
                    item {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Close details",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

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
        ),
        accentColor = Color(0xFF34D399) // Mint / Emerald Green
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
        ),
        accentColor = Color(0xFF38BDF8) // Sky / Cyan Blue
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
        ),
        accentColor = Color(0xFF2DD4BF) // Teal / Turquoise
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
        ),
        accentColor = Color(0xFFFB923C) // Amber / Vibrant Orange
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
        ),
        accentColor = Color(0xFFA78BFA) // Soft Violet / Purple
    )
)

// =========================================================================
// REUSABLE BRAND COMPONENTS (EXACT LOGO & TYPOGRAPHY)
// =========================================================================

@Composable
fun VaanBrandLogo(modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Interactive 3D Spring Scale Animation on Press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoPressScale"
    )

    // Hi-tech breathing ambient glow pulse
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlowTransition")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoGlowAlpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        // High-Tech iOS Ambient Backglow Ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2DD4BF).copy(alpha = glowAlpha * 0.65f),
                            Color(0xFF10B981).copy(alpha = glowAlpha * 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Glassy iOS Squircle Icon Badge with 3D Border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color(0xFF2DD4BF).copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.20f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.30f),
                                Color.Transparent
                            )
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx(), 20.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon_hitech_1785459738631),
                contentDescription = "Vaan Hi-Tech Icon",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
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
                IphoneGlassyBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(280, easing = LinearOutSlowInEasing)) +
                            scaleIn(initialScale = 0.97f, animationSpec = tween(280))) togetherWith
                            (fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing)) +
                                scaleOut(targetScale = 1.02f, animationSpec = tween(200)))
                    },
                    label = "pageTabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        "home" -> HomeScreen(
                            meetings = meetings,
                            inquiries = inquiries,
                            appointments = appointments,
                            emailLogs = emailLogs,
                            autoEmailEnabled = autoEmailEnabled,
                            onNavigateToTab = { currentTab = it },
                            onBookServiceCategory = { serviceTitle ->
                                selectedServiceCategoryForBooking = serviceTitle
                                currentTab = "bookings"
                            }
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
}

// =========================================================================
// IPHONE GLASSY BOTTOM NAVIGATION BAR COMPOSABLE
// =========================================================================
data class NavTabItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun IphoneGlassyBottomBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val navItems = listOf(
        NavTabItem("home", "Home", Icons.Default.Dashboard, "nav_home"),
        NavTabItem("services", "Services", Icons.Default.CloudQueue, "nav_services"),
        NavTabItem("insights", "Blog", Icons.Default.Language, "nav_insights"),
        NavTabItem("chatbot", "VaanAI Chat", Icons.Default.Chat, "nav_chatbot"),
        NavTabItem("bookings", "Bookings", Icons.Default.Schedule, "nav_bookings")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xDD0B132B), // Frosted Glassy Dark Slate
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Top edge glass light-reflection highlight
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.06f),
                                Color(0x202DD4BF)
                            )
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(32.dp.toPx(), 32.dp.toPx())
                    )
                }
                .border(
                    width = 1.25.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.45f),
                            Color(0x402DD4BF),
                            Color.White.copy(alpha = 0.12f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.id

                    // Animated Icon Scale with spring physics
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.20f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "navIconScale"
                    )

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val pressScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.90f else 1.0f,
                        animationSpec = spring(stiffness = Spring.StiffnessHigh),
                        label = "navPressScale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .testTag(item.testTag)
                            .graphicsLayer {
                                scaleX = pressScale
                                scaleY = pressScale
                            }
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x552DD4BF),
                                            Color(0x2510B981)
                                        )
                                    )
                                } else {
                                    SolidColor(Color.Transparent)
                                }
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                brush = if (isSelected) {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xA02DD4BF),
                                            Color(0x5010B981)
                                        )
                                    )
                                } else {
                                    SolidColor(Color.Transparent)
                                },
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onTabSelected(item.id) }
                            )
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) Color(0xFF2DD4BF) else Color(0xFF94A3B8).copy(alpha = 0.65f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8).copy(alpha = 0.65f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2DD4BF))
                                )
                            }
                        }
                    }
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
    onNavigateToTab: (String) -> Unit,
    onBookServiceCategory: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedPillarForSubpage by remember { mutableStateOf<PillarItem?>(null) }
    var selectedCapabilityForSubpage by remember { mutableStateOf<VaanCapabilityItem?>(null) }
    var selectedMetricForSubpage by remember { mutableStateOf<MetricDetailItem?>(null) }

    // Subpage Dialog Displays
    selectedPillarForSubpage?.let { pillar ->
        PillarDetailSubpageDialog(
            pillar = pillar,
            onDismiss = { selectedPillarForSubpage = null },
            onBookConsultation = { category ->
                onBookServiceCategory(category)
            }
        )
    }

    selectedCapabilityForSubpage?.let { capability ->
        CapabilityDetailSubpageDialog(
            capability = capability,
            onDismiss = { selectedCapabilityForSubpage = null },
            onBookConsultation = { category ->
                onBookServiceCategory(category)
            }
        )
    }

    selectedMetricForSubpage?.let { metric ->
        MetricDetailSubpageDialog(
            metricItem = metric,
            onDismiss = { selectedMetricForSubpage = null },
            onBookConsultation = { category ->
                onBookServiceCategory(category)
            }
        )
    }

    val valuePillars = listOf(
        PillarItem(
            num = "01",
            title = "Strategic ownership",
            desc = "Complex business requirements translated into scalable, secure, cost-effective architectures.",
            accentColor = Color(0xFF38BDF8),
            overview = "At VAAN, strategic ownership means bridging executive vision with hands-on engineering. We don't just supply static recommendation slides — we architect, validate, and govern production systems with direct accountability for performance, cost, and security.",
            objectives = listOf(
                PillarObjective("Target Operating Model", "Designing resilient, cloud-agnostic blueprints adapted to enterprise constraints and regulatory frameworks."),
                PillarObjective("FinOps & Cost Governance", "Eliminating wasted cloud spend through intelligent resource scheduling, right-sizing, and reserved capacity."),
                PillarObjective("Enterprise Risk Mitigation", "Embedding zero-trust security architecture, data sovereignty, and compliance controls from day zero.")
            ),
            roadmap = listOf(
                PillarPhase("01", "Discovery & Audit", "Comprehensive evaluation of existing legacy bottlenecks, technical debt, and cloud spend vectors."),
                PillarPhase("02", "Blueprint & Governance", "Defining target topology, automated guardrails, security baselines, and cross-team delivery standards."),
                PillarPhase("03", "Hands-On Execution", "Embedded architecture leadership working alongside engineering teams to ship iterative releases."),
                PillarPhase("04", "Continuous Optimization", "Automated FinOps monitoring, telemetry benchmarking, and ongoing operational review.")
            ),
            deliverables = listOf(
                "Enterprise Multi-Cloud Target Architecture Blueprints",
                "FinOps Cloud Cost Governance & Optimization Matrix",
                "Zero-Trust Security & Regulatory Compliance Baselines",
                "Technical Debt Remediation & Modernization Roadmap"
            )
        ),
        PillarItem(
            num = "02",
            title = "Delivery confidence",
            desc = "Full lifecycle delivery led against business and regulatory standards — not just advisory slides.",
            accentColor = Color(0xFF34D399),
            overview = "Large scale transformations fail when strategy disconnects from execution. VAAN leads full-lifecycle delivery against strict business and regulatory standards, using SAFe 6 practices to maintain velocity without sacrificing quality.",
            objectives = listOf(
                PillarObjective("Predictable Velocity", "Establishing clear program increment cadence, dependency mapping, and transparent risk management."),
                PillarObjective("Regulatory & Compliance Readiness", "Ensuring strict adherence to APRA, RBNZ, SOC 2, and ISO 27001 standards throughout the lifecycle."),
                PillarObjective("Technical Debt Remediation", "Systematically refactoring legacy bottlenecks while maintaining continuous feature delivery cadence.")
            ),
            roadmap = listOf(
                PillarPhase("01", "Agile Alignment", "Implementing SAFe 6 PI planning, team backlogs, and cross-functional dependency tracking."),
                PillarPhase("02", "Automated Quality Gates", "Integrating CI/CD testing, SAST/DAST security scanning, and policy-as-code into release pipelines."),
                PillarPhase("03", "Milestone Rollouts", "Incremental canary and blue/green deployments with automated rollback capabilities."),
                PillarPhase("04", "Team Elevation", "Upskilling internal engineering teams to ensure long-term operational self-sufficiency.")
            ),
            deliverables = listOf(
                "SAFe 6 Program Increment Delivery & Governance Plan",
                "Automated DevSecOps Pipeline & Security Quality Gates",
                "Cross-Team Risk & Dependency Heatmaps",
                "Operational Readiness & Production Cutover Playbooks"
            )
        ),
        PillarItem(
            num = "03",
            title = "Technical credibility",
            desc = "Hands-on cloud, data and distributed-systems expertise that validates designs and reduces delivery risk.",
            accentColor = Color(0xFFA78BFA),
            overview = "Our recommendations are backed by 16+ years of hands-on distributed systems and cloud data platform engineering. We write production Kotlin, Java, Terraform, SQL, and Python alongside your senior engineers to validate designs in code.",
            objectives = listOf(
                PillarObjective("Production-Tested Design", "Architecting high-throughput, fault-tolerant event-driven microservices for scale."),
                PillarObjective("Modern Data Ecosystems", "Building clean ETL/ELT streaming data pipelines with Snowflake, Databricks, and Microsoft Fabric."),
                PillarObjective("Infrastructure as Code", "Enforcing reproducible, immutable infrastructure modules across AWS, Azure, and GCP.")
            ),
            roadmap = listOf(
                PillarPhase("01", "Proof of Concept", "Rapid technical prototyping to validate throughput, latency, and cloud unit costs."),
                PillarPhase("02", "Cloud Native Core", "Deploying containerized microservices on Kubernetes with service mesh traffic control."),
                PillarPhase("03", "Data Lakehouse Pipeline", "Implementing dbt transformations, Spark batching, and Kafka event streams."),
                PillarPhase("04", "Full Observability", "Instrumenting OpenTelemetry, Prometheus, and Grafana dashboards for end-to-end tracing.")
            ),
            deliverables = listOf(
                "Production-Grade Microservice & Event Broker Architectures",
                "High-Performance Lakehouse Data Pipelines (dbt, Spark, Kafka)",
                "Terraform & Bicep Infrastructure as Code Repositories",
                "End-to-End OpenTelemetry APM & Tracing Dashboards"
            )
        )
    )

    val capabilitiesList = listOf(
        VaanCapabilityItem(
            icon = Icons.Default.CloudQueue,
            title = "Multi-cloud architecture",
            desc = "Production-grade, cost-optimised solutions designed across AWS, Azure and GCP — reducing vendor lock-in while maximising the value of each platform.",
            tags = listOf("AWS", "Azure", "GCP", "FinOps"),
            accentColor = Color(0xFF38BDF8),
            overview = "Production-grade multi-cloud architectures built across AWS, Azure, and GCP. We help enterprise organizations leverage the best capabilities of each cloud provider while establishing automated governance, unified security, and rigorous FinOps spend controls.",
            solutions = listOf(
                CapabilitySolution("Cloud Migration & Landing Zones", "Automated, secure landing zone blueprints with automated multi-account guardrails."),
                CapabilitySolution("FinOps & Cost Governance", "Real-time visibility, cost allocation tagging, and automated resource right-sizing."),
                CapabilitySolution("Hybrid & Disaster Recovery", "Multi-region active-active topologies and zero-downtime failover strategies."),
                CapabilitySolution("Infrastructure as Code (IaC)", "Declarative Terraform and Pulumi modules for deterministic infrastructure deployments.")
            ),
            techStackDetails = mapOf(
                "AWS" to "EKS, Lambda, S3, RDS, Transit Gateway, CloudFront",
                "Azure" to "AKS, CosmosDB, ExpressRoute, Synapse, App Service",
                "GCP" to "GKE, BigQuery, Cloud Run, Pub/Sub, Anthos",
                "Tooling" to "Terraform, Pulumi, HashiCorp Vault, Datadog, Kubecost"
            ),
            strategicOutcomes = listOf(
                "35%+ Reduction in Idle Multi-Cloud Infrastructure Spend",
                "Zero Vendor Lock-In for Core Business Logic & Microservices",
                "99.99% Multi-Region High Availability SLA",
                "100% Automated Infrastructure Deployment via GitOps"
            )
        ),
        VaanCapabilityItem(
            icon = Icons.Default.Storage,
            title = "Data platform modernisation",
            desc = "ETL/ELT pipelines and analytics platforms built on Snowflake, Databricks and Microsoft Fabric that turn raw, fragmented data into decisions teams can act on.",
            tags = listOf("Snowflake", "Databricks", "Fabric", "DBT"),
            accentColor = Color(0xFF34D399),
            overview = "Transform raw, fragmented enterprise data into structured, real-time intelligence. VAAN modernizes legacy data warehouses into modern lakehouses powered by Snowflake, Databricks, and Microsoft Fabric.",
            solutions = listOf(
                CapabilitySolution("Lakehouse Architecture", "Unifying batch and streaming data using open formats (Delta Lake, Apache Iceberg)."),
                CapabilitySolution("Real-Time Streaming Pipelines", "Event-driven data processing pipelines with Kafka, Spark Streaming, and dbt."),
                CapabilitySolution("Data Mesh & Governance", "Decentralized data product ownership with automated cataloging and data lineage."),
                CapabilitySolution("Analytics & AI Integration", "High-performance query engine tuning and feature store pipelines for AI models.")
            ),
            techStackDetails = mapOf(
                "Platforms" to "Snowflake, Databricks, Microsoft Fabric, BigQuery",
                "Transformation" to "dbt Core / Cloud, Apache Spark, Airflow, Dagster",
                "Streaming" to "Apache Kafka, Confluent Cloud, Azure Event Hubs",
                "Governance" to "Collibra, Unity Catalog, Alation, Apache Atlas"
            ),
            strategicOutcomes = listOf(
                "10x Query Acceleration for Enterprise BI & Reporting Dashboards",
                "Automated Data Governance & Lineage for Audit Auditing",
                "Single Unified Data Lakehouse for Analytics & Predictive ML",
                "Near-Zero Latency Real-Time Streaming Data Pipelines"
            )
        ),
        VaanCapabilityItem(
            icon = Icons.Default.DeveloperMode,
            title = "Distributed systems & microservices",
            desc = "Event-driven, fault-tolerant systems architected for enterprise-scale workloads, drawing on deep Java and microservices expertise.",
            tags = listOf("Java", "Spring Boot", "Kubernetes", "EDA"),
            accentColor = Color(0xFFFB923C),
            overview = "Designing and engineering enterprise distributed systems built for extreme scalability and fault tolerance. Drawing on deep Java, Spring Boot, and Kubernetes expertise, we replace monolithic legacy bottlenecks with decoupled microservices.",
            solutions = listOf(
                CapabilitySolution("Event-Driven Architecture (EDA)", "Asynchronous messaging with event sourcing and CQRS patterns for high-concurrency workloads."),
                CapabilitySolution("Container Orchestration & Mesh", "Enterprise Kubernetes (EKS/AKS/GKE) with Istio service mesh and GitOps deployments."),
                CapabilitySolution("Resilience & Circuit Breaking", "Distributed tracing, rate limiting, circuit breakers, and graceful fallback mechanisms."),
                CapabilitySolution("API Gateway & Security", "Centralized OAuth2/OIDC authentication, rate limiting, and GraphQL/REST endpoints.")
            ),
            techStackDetails = mapOf(
                "Languages" to "Java 21+, Kotlin, Go, TypeScript",
                "Frameworks" to "Spring Boot 3, Quarkus, Micronaut, Ktor",
                "Orchestration" to "Kubernetes, Helm, ArgoCD, Flux, Istio",
                "Messaging" to "Apache Kafka, RabbitMQ, AWS SQS/SNS, NATS"
            ),
            strategicOutcomes = listOf(
                "Sub-10ms P99 Latency under High Concurrency Spikes",
                "Zero-Downtime Continuous Deployments via ArgoCD GitOps",
                "Fault-Tolerant System Resilience with Automated Healing",
                "Decoupled Microservice Architecture with Independent Scale"
            )
        ),
        VaanCapabilityItem(
            icon = Icons.Default.Verified,
            title = "Governance & delivery leadership",
            desc = "Cross-functional teams led through SAFe 6 practice — managing technical debt and navigating architecture governance in regulated sectors.",
            tags = listOf("SAFe 6", "Agile", "Risk & Compliance"),
            accentColor = Color(0xFFA78BFA),
            overview = "Navigating complex corporate governance, risk compliance, and multi-vendor delivery. VAAN provides hands-on technical leadership to guide cross-functional teams through SAFe 6 practices while managing technical debt and meeting regulatory audits.",
            solutions = listOf(
                CapabilitySolution("SAFe 6 Program Execution", "Agile Release Train (ART) setup, PI planning, and value stream mapping for large teams."),
                CapabilitySolution("Regulatory & Audit Compliance", "Automated compliance evidence collection for banking (APRA/RBNZ), energy, and government."),
                CapabilitySolution("Technical Debt Remediation", "Quantitative tech debt evaluation, priority refactoring backlogs, and risk scoring."),
                CapabilitySolution("Vendor & Team Alignment", "Aligning multi-vendor engineering teams around unified architecture standards and SLAs.")
            ),
            techStackDetails = mapOf(
                "Frameworks" to "SAFe 6, Scrum, Kanban, LeSS, Value Stream Management",
                "Compliance" to "ISO 27001, SOC 2 Type II, APRA CPS 234, RBNZ Framework",
                "Quality & Security" to "SonarQube, Snyk, HashiCorp Vault, GitHub Security"
            ),
            strategicOutcomes = listOf(
                "100% Audit Readiness for Banking & Energy Industry Standards",
                "40% Fast-Tracked Release Cadence through DevSecOps Automation",
                "Reduced Cross-Team Bottlenecks and Delivery Friction",
                "Transparent Engineering Metrics & Executive Visibility"
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            val heroAccent = Color(0xFF2DD4BF)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Soft outer border backlight glow
                        drawRoundRect(
                            color = heroAccent.copy(alpha = 0.22f),
                            topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(26.dp.toPx(), 26.dp.toPx())
                        )
                        // Ambient radial backlight glow behind card
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(heroAccent.copy(alpha = 0.20f), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.2f),
                                radius = size.width * 0.8f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx(), 24.dp.toPx())
                        )
                    }
                    .border(
                        width = 1.25.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                heroAccent.copy(alpha = 0.60f),
                                heroAccent.copy(alpha = 0.20f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E293B)
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
                        text = "Independent IT consultancy led by Vasanth N — a SAFe 6 Agilist with 16+ years architecting cloud-native data platforms for banking, energy and automotive enterprises. Strategy through to production, across AWS, Azure, GCP, Snowflake, Databricks and Microsoft Fabric.",
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
                        text = "VAAN Consulting is the independent practice of Vasanth N, a SAFe 6 Agilist who has spent over sixteen years translating business strategy into cloud-native, data-driven platforms for banking, automotive and energy & utilities organisations.",
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
                    
                    // Value pillars (01, 02, 03) with distinct category colors & subpage handlers
                    valuePillars.forEach { pillar ->
                        InteractiveValuePillarRow(
                            pillar = pillar,
                            onClick = { selectedPillarForSubpage = pillar }
                        )
                    }
                }
            }
        }

        // Capabilities Block
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

        // The 4 key areas of capability with interactive mouseover backlight glow & subpage handlers
        capabilitiesList.forEach { quad ->
            item {
                InteractiveCapabilityCard(
                    quad = quad,
                    onClick = { selectedCapabilityForSubpage = quad }
                )
            }
        }

        // Interactive Technology Hub Diagram
        item {
            InteractiveDiagramCard()
        }

        // Operational Metrics Grid (from website screenshot)
        val metricsList = listOf(
            MetricDetailItem(
                metric = "16+",
                label = "Years delivering cloud platforms",
                color = Color(0xFF38BDF8),
                title = "16+ Years Enterprise Cloud Delivery",
                subtitle = "Proven track record of architecting, deploying, and governing mission-critical systems across enterprise environments.",
                overview = "Since 2008, VAAN has led multi-cloud migrations, high-throughput distributed systems engineering, and modern data stack implementations for top-tier enterprise clients across Australia and New Zealand.",
                keyPoints = listOf(
                    "2008–2014" to "High-concurrency Java & J2EE distributed systems for tier-1 financial institutions.",
                    "2014–2018" to "Enterprise AWS & Azure Cloud Migration Lead, modernizing legacy monolithic infrastructure.",
                    "2018–2022" to "Multi-cloud Landing Zones, zero-trust security baselines, and Kubernetes microservices.",
                    "2022–Present" to "Snowflake, Databricks, and Microsoft Fabric Lakehouse data stack implementations."
                ),
                outcomes = listOf(
                    "100+ Enterprise Cloud Systems Architected & Deployed",
                    "Zero Outages or Data Loss during Legacy System Cutover",
                    "100% On-Time Milestone Delivery across SAFe 6 Program Increments"
                )
            ),
            MetricDetailItem(
                metric = "3",
                label = "Sectors: banking, energy, auto",
                color = Color(0xFF34D399),
                title = "Core Industry Sectors",
                subtitle = "Specialized domain engineering operating within highly regulated, compliance-heavy enterprise sectors.",
                overview = "Our team brings hands-on domain experience navigating complex corporate environments, strict regulatory oversight, and multi-vendor delivery pipelines.",
                keyPoints = listOf(
                    "Banking & Financial Services" to "APRA CPS 234 / RBNZ compliance, core banking API integrations, real-time fraud detection pipelines.",
                    "Energy & Utilities" to "High-frequency smart meter ingestion, IoT grid telemetry, real-time energy trading data streaming.",
                    "Automotive & Supply Chain" to "Connected vehicle telematics, inventory optimization, multi-region ERP data synchronization."
                ),
                outcomes = listOf(
                    "100% Regulatory Audit Readiness across Banking & Energy Standards",
                    "Full APRA CPS 234 & RBNZ Governance Framework Alignment",
                    "Resilient High-Throughput Ingestion for IoT & Telemetry"
                )
            ),
            MetricDetailItem(
                metric = "30%",
                label = "Typical processing reduction",
                color = Color(0xFFFB923C),
                title = "Processing & Cost Optimization",
                subtitle = "Quantified architectural optimization that significantly slashes compute overhead and cloud operational spend.",
                overview = "We identify structural bottlenecks in cloud resources, query plans, and event brokers — eliminating waste and engineering high-efficiency pipelines.",
                keyPoints = listOf(
                    "FinOps & Resource Right-Sizing" to "Automated cluster scaling, spot instance orchestration, and eliminating idle cloud compute capacity.",
                    "Query & Lakehouse Tuning" to "Optimizing dbt SQL transformations, Snowflake warehouse auto-suspend rules, and Delta Lake partitioning.",
                    "Event-Driven Decoupling" to "Replacing resource-heavy synchronous polling with high-efficiency async Kafka & RabbitMQ event streams."
                ),
                outcomes = listOf(
                    "30%–45% Average Reduction in Monthly Cloud Infrastructure Spend",
                    "10x Processing Speedup for Nightly Enterprise Batch ETL Jobs",
                    "60% Reduced P99 API Response Latency for Microservice Gateways"
                )
            ),
            MetricDetailItem(
                metric = "28",
                label = "Professional tech certs held",
                color = Color(0xFFA78BFA),
                title = "Certified Engineering Credentials",
                subtitle = "Industry-recognized professional certifications across major cloud providers, data platforms, and agile frameworks.",
                overview = "Continuous technical mastery ensures our architectural designs adhere strictly to vendor best practices, security standards, and modern patterns.",
                keyPoints = listOf(
                    "AWS Certifications" to "Solutions Architect Professional, DevOps Engineer Professional, Data Analytics Specialty.",
                    "Azure & GCP Certifications" to "Azure Solutions Architect Expert, GCP Professional Cloud Architect.",
                    "Data & AI Certifications" to "Snowflake Certified SnowPro Core, Databricks Certified Data Engineer Professional.",
                    "Agile & Governance" to "SAFe 6 Practice Consultant (SPC), Certified ScrumMaster (CSM)."
                ),
                outcomes = listOf(
                    "Verified Expertise across Multi-Cloud Architectures",
                    "Strict Adherence to Cloud Provider Well-Architected Frameworks",
                    "Rapid Technical Onboarding and Architectural Credibility"
                )
            )
        )

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
                    metric = metricsList[0].metric,
                    label = metricsList[0].label,
                    color = metricsList[0].color,
                    onClick = { selectedMetricForSubpage = metricsList[0] }
                )
                MetricItemCard(
                    modifier = Modifier.weight(1f),
                    metric = metricsList[1].metric,
                    label = metricsList[1].label,
                    color = metricsList[1].color,
                    onClick = { selectedMetricForSubpage = metricsList[1] }
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
                    metric = metricsList[2].metric,
                    label = metricsList[2].label,
                    color = metricsList[2].color,
                    onClick = { selectedMetricForSubpage = metricsList[2] }
                )
                MetricItemCard(
                    modifier = Modifier.weight(1f),
                    metric = metricsList[3].metric,
                    label = metricsList[3].label,
                    color = metricsList[3].color,
                    onClick = { selectedMetricForSubpage = metricsList[3] }
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
                        color = Color(0xFF2DD4BF),
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/vasanth-vimal/"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open LinkedIn", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "LinkedIn",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LinkedIn (vasanth-vimal)",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:vaanconsulting@gmail.com"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open email application", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "vaanconsulting@gmail.com",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+64225601989"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open dialer", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Phone",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+64 22 560 1989",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vaanconsulting.com"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open browser", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Website",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "vaanconsulting.com",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
fun MetricItemCard(
    modifier: Modifier = Modifier,
    metric: String,
    label: String,
    color: Color,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = isHovered || isPressed

    val animatedBorderAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.90f else 0.45f,
        animationSpec = tween(durationMillis = 250),
        label = "metricBorderAlpha"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .drawBehind {
                // Soft outer border backlight glow with matching metric color
                drawRoundRect(
                    color = color.copy(alpha = if (isActive) 0.32f else 0.14f),
                    topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx(), 22.dp.toPx())
                )
                // Radial backlight ambient glow behind card
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = if (isActive) 0.38f else 0.16f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.3f),
                        radius = size.width * 0.75f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx(), 20.dp.toPx())
                )
            }
            .border(
                width = if (isActive) 2.dp else 1.25.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        color.copy(alpha = animatedBorderAlpha),
                        color.copy(alpha = animatedBorderAlpha * 0.35f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .hoverable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = metric,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
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
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color.copy(alpha = if (isActive) 0.25f else 0.12f))
                    .border(1.dp, color.copy(alpha = 0.35f), CircleShape)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View detail",
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
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
    
    val hubAccent = Color(0xFF38BDF8) // Sky cyan glow
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = hubAccent.copy(alpha = 0.22f),
                    topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(26.dp.toPx(), 26.dp.toPx())
                )
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(hubAccent.copy(alpha = 0.25f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.3f),
                        radius = size.width * 0.8f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx(), 24.dp.toPx())
                )
            }
            .border(
                width = 1.25.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        hubAccent.copy(alpha = 0.65f),
                        hubAccent.copy(alpha = 0.20f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
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
            
            Spacer(modifier = Modifier.height(14.dp))
            val nodeAccent = when (selectedNode) {
                "GCP", "Databricks", "AWS" -> Color(0xFF14B8A6)
                "Snowflake", "Fabric", "Azure" -> Color(0xFFF59E0B)
                else -> Color(0xFF38BDF8)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Soft outer border backlight glow
                        drawRoundRect(
                            color = nodeAccent.copy(alpha = 0.32f),
                            topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx(), 18.dp.toPx())
                        )
                        // Ambient radial backlight glow inside box
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(nodeAccent.copy(alpha = 0.35f), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.5f),
                                radius = size.width * 0.85f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
                        )
                    }
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                nodeAccent.copy(alpha = 0.90f),
                                nodeAccent.copy(alpha = 0.30f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = selectedNode,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = nodeAccent
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = nodeDescriptions[selectedNode] ?: "",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.92f),
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveServiceCard(
    service: VaanService,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = isHovered || isPressed

    val accent = service.accentColor

    val animatedBorderAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.95f else 0.50f,
        animationSpec = tween(durationMillis = 250),
        label = "serviceBorderAlpha"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Soft outer border backlight glow with matching accent color
                drawRoundRect(
                    color = accent.copy(alpha = if (isActive) 0.38f else 0.18f),
                    topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx(), 22.dp.toPx())
                )
                // Radial backlight ambient glow behind card
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = if (isActive) 0.42f else 0.20f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.3f),
                        radius = size.width * 0.8f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx(), 20.dp.toPx())
                )
            }
            .border(
                width = if (isActive) 2.dp else 1.25.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = animatedBorderAlpha),
                        accent.copy(alpha = animatedBorderAlpha * 0.35f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .hoverable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("service_card_${service.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f))
                        .border(1.dp, accent.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(service.icon, service.title, tint = accent, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = accent
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = service.subtitle,
                        fontSize = 11.sp,
                        color = accent.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(accent.copy(alpha = if (isActive) 0.25f else 0.12f))
                        .border(1.dp, accent.copy(alpha = 0.35f), CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View detail",
                        tint = accent,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = service.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                service.platforms.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent.copy(alpha = 0.12f))
                            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(tag, fontSize = 9.sp, color = accent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. SERVICES SCREEN COMPOSABLE (With Custom Color Headings, Glow Backlight Cards, and Detail Overlays)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    bookmarkedServices: Set<String>,
    onToggleBookmark: (String) -> Unit,
    onBookService: (String) -> Unit
) {
    var selectedServiceForDetails by remember { mutableStateOf<VaanService?>(null) }

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
            modifier = Modifier.padding(bottom = 14.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(servicesList) { service ->
                InteractiveServiceCard(
                    service = service,
                    onClick = { selectedServiceForDetails = service }
                )
            }
        }
    }

    // Service Detail Overlay Subpage
    selectedServiceForDetails?.let { service ->
        val accent = service.accentColor
        Dialog(
            onDismissRequest = { selectedServiceForDetails = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, accent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.88f)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(accent.copy(alpha = 0.2f))
                                    .border(1.dp, accent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(service.icon, service.title, tint = accent, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = service.title,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    lineHeight = 21.sp,
                                    color = accent
                                )
                                Text(
                                    text = service.subtitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { selectedServiceForDetails = null },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = service.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "TECHNICAL SCOPE & DELIVERABLES",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            color = accent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        service.details.forEach { bullet ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(bullet, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { selectedServiceForDetails = null },
                        colors = ButtonDefaults.buttonColors(containerColor = accent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Close details", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveArticleCard(
    article: TechArticle,
    isSaved: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = isHovered || isPressed

    val accent = when (article.category) {
        "Data Platform" -> Color(0xFF34D399) // Mint / Emerald Green
        "Cloud & Digital" -> Color(0xFF38BDF8) // Sky Blue
        "Mobile Dev" -> Color(0xFF2DD4BF) // Teal
        "Web Dev" -> Color(0xFFFB923C) // Amber / Orange
        "Bespoke Consulting" -> Color(0xFFA78BFA) // Violet / Purple
        else -> Color(0xFF38BDF8)
    }

    val animatedBorderAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.95f else 0.50f,
        animationSpec = tween(durationMillis = 250),
        label = "articleBorderAlpha"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Outer glow backlight border matching category accent color
                drawRoundRect(
                    color = accent.copy(alpha = if (isActive) 0.38f else 0.18f),
                    topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx(), 22.dp.toPx())
                )
                // Radial backlight ambient glow behind card
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = if (isActive) 0.42f else 0.20f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.3f),
                        radius = size.width * 0.8f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx(), 20.dp.toPx())
                )
            }
            .border(
                width = if (isActive) 2.dp else 1.25.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = animatedBorderAlpha),
                        accent.copy(alpha = animatedBorderAlpha * 0.35f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .hoverable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("article_card_${article.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.15f))
                        .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = article.category,
                        fontSize = 10.sp,
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = article.readTime,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(28.dp).testTag("bookmark_article_${article.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isSaved) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = article.title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = article.summary,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = article.date,
                fontSize = 10.sp,
                color = accent.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold
            )
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
    var isSearchFocused by remember { mutableStateOf(false) }
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
            placeholder = {
                if (!isSearchFocused) {
                    Text("Search technical articles...")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isSearchFocused = it.isFocused }
                .testTag("blog_search_input"),
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
                        color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredArticles) { article ->
                    val isSaved = bookmarkedArticles.contains(article.id)
                    InteractiveArticleCard(
                        article = article,
                        isSaved = isSaved,
                        onToggleBookmark = { onToggleBookmark(article.id) },
                        onClick = { selectedArticleForReading = article }
                    )
                }
            }
        }
    }

    // Article Reading Dialog Overlay
    selectedArticleForReading?.let { article ->
        val isSaved = bookmarkedArticles.contains(article.id)
        val categoryAccent = when (article.category) {
            "Data Platform" -> Color(0xFF34D399) // Mint / Emerald Green
            "Cloud & Digital" -> Color(0xFF38BDF8) // Sky Blue
            "Mobile Dev" -> Color(0xFF2DD4BF) // Teal
            "Web Dev" -> Color(0xFFFB923C) // Amber / Orange
            "Bespoke Consulting" -> Color(0xFFA78BFA) // Violet / Purple
            else -> Color(0xFF38BDF8)
        }

        Dialog(
            onDismissRequest = { selectedArticleForReading = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.88f)
                    .drawBehind {
                        drawRoundRect(
                            color = categoryAccent.copy(alpha = 0.25f),
                            topLeft = androidx.compose.ui.geometry.Offset(-2.dp.toPx(), -2.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(26.dp.toPx(), 26.dp.toPx())
                        )
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(categoryAccent.copy(alpha = 0.22f), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.2f),
                                radius = size.width * 0.8f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx(), 24.dp.toPx())
                        )
                    }
                    .border(
                        width = 1.25.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                categoryAccent.copy(alpha = 0.70f),
                                categoryAccent.copy(alpha = 0.25f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(categoryAccent.copy(alpha = 0.15f))
                                    .border(1.dp, categoryAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = article.category,
                                    fontSize = 10.sp,
                                    color = categoryAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = article.readTime,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onToggleBookmark(article.id) }) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isSaved) categoryAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = article.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Published on ${article.date} by Vaan Systems Advisory",
                        fontSize = 10.sp,
                        color = categoryAccent.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Full Article Content
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, categoryAccent.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = article.content,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.92f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedArticleForReading = null },
                        colors = ButtonDefaults.buttonColors(containerColor = categoryAccent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Close article",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
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
    var isChatFocused by remember { mutableStateOf(false) }

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
                            "How does VAAN Consultancy support SAFe 6 transformations?",
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
                placeholder = {
                    if (!isChatFocused) {
                        Text("Ask about our architectures...")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isChatFocused = it.isFocused }
                    .testTag("chat_text_input"),
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

    // Focus States
    var isNameFocused by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isCompanyFocused by remember { mutableStateOf(false) }
    var isMessageFocused by remember { mutableStateOf(false) }
    
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

                    // WEBSITE
                    Column(modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vaanconsulting.com"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Could not open browser", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("WEBSITE", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("vaanconsulting.com", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                        color = Color(0xFF2DD4BF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
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
                            OutlinedTextField(
                                value = name,
                                onValueChange = { 
                                    if (it.length <= 50) {
                                        name = it 
                                        if (it.isNotBlank()) nameError = null
                                    }
                                },
                                label = { Text("Your Name *") },
                                isError = nameError != null,
                                supportingText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(nameError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                        Text("${name.length}/50", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { isNameFocused = it.isFocused }
                                    .testTag("inquiry_name"),
                                placeholder = {
                                    if (!isNameFocused) {
                                        Text("e.g. John Doe", fontSize = 13.sp)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            // EMAIL ADDRESS *
                            OutlinedTextField(
                                value = email,
                                onValueChange = { 
                                    if (it.length <= 50) {
                                        email = it 
                                        if (it.isNotBlank() && isValidEmail(it)) emailError = null
                                    }
                                },
                                label = { Text("Email Address *") },
                                isError = emailError != null,
                                supportingText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(emailError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                        Text("${email.length}/50", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { isEmailFocused = it.isFocused }
                                    .testTag("inquiry_email"),
                                placeholder = {
                                    if (!isEmailFocused) {
                                        Text("e.g. john@company.com", fontSize = 13.sp)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            // COMPANY / ORG
                            OutlinedTextField(
                                value = company,
                                onValueChange = { 
                                    if (it.length <= 50) {
                                        company = it 
                                    }
                                },
                                label = { Text("Company / Org") },
                                supportingText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text("${company.length}/50", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { isCompanyFocused = it.isFocused }
                                    .testTag("inquiry_company"),
                                placeholder = {
                                    if (!isCompanyFocused) {
                                        Text("e.g. Acme Corp", fontSize = 13.sp)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            // CONSULTING SERVICE REQUIRED
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dropdownExpanded = !dropdownExpanded }
                            ) {
                                val menuWidth = maxWidth
                                OutlinedTextField(
                                    value = selectedService,
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("Consulting Service Required *") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    onDismissRequest = { dropdownExpanded = false },
                                    modifier = Modifier.width(menuWidth)
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

                            // MESSAGE DESCRIPTION *
                            OutlinedTextField(
                                value = messageDescription,
                                onValueChange = { 
                                    if (it.length <= 250) {
                                        messageDescription = it 
                                        if (it.isNotBlank()) messageError = null
                                    }
                                },
                                label = { Text("Message Description *") },
                                isError = messageError != null,
                                supportingText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(messageError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                        Text("${messageDescription.length}/250", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .onFocusChanged { isMessageFocused = it.isFocused }
                                    .testTag("inquiry_message"),
                                placeholder = {
                                    if (!isMessageFocused) {
                                        Text("Outline your technical bottlenecks, platform goals, or timeline boundaries...", fontSize = 13.sp)
                                    }
                                },
                                maxLines = 5,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    var hasError = false
                                    
                                    if (name.isBlank()) {
                                        nameError = "Name is required"
                                        hasError = true
                                    } else if (name.length > 50) {
                                        nameError = "Name must be max 50 characters"
                                        hasError = true
                                    } else {
                                        nameError = null
                                    }

                                    if (email.isBlank()) {
                                        emailError = "Corporate email is required"
                                        hasError = true
                                    } else if (email.length > 50) {
                                        emailError = "Email must be max 50 characters"
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
                                    } else if (messageDescription.length > 250) {
                                        messageError = "Message must be max 250 characters"
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
                        Text("Chat instantly with Vasanth N on +64 22 560 1989", fontSize = 12.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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

    // Set up native DatePickerDialog in NZST (Pacific/Auckland) timezone
    val nzTimeZone = java.util.TimeZone.getTimeZone("Pacific/Auckland")
    val calendar = Calendar.getInstance(nzTimeZone)
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance(nzTimeZone)
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.US).apply {
                timeZone = nzTimeZone
            }
            proposedDate = sdf.format(cal.time)
            dateError = null // Clear error when selected
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() - 1000
    }

    // Set up native TimePickerDialog in NZST (Pacific/Auckland) timezone
    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val cal = Calendar.getInstance(nzTimeZone)
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat("hh:mm a", Locale.US).apply {
                timeZone = nzTimeZone
            }
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
                        if (it.length <= 50) {
                            name = it 
                            if (it.isNotBlank()) nameError = null
                        }
                    },
                    label = { Text("Your Name *") },
                    isError = nameError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(nameError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            Text("${name.length}/50", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("booking_name_input"),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        if (it.length <= 50) {
                            email = it 
                            if (it.isNotBlank() && isValidEmail(it)) emailError = null
                        }
                    },
                    label = { Text("Corporate Email Address *") },
                    isError = emailError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(emailError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            Text("${email.length}/50", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
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
                                Text(cat, color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
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
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                    onValueChange = { 
                        if (it.length <= 250) {
                            notes = it 
                        }
                    },
                    label = { Text("Describe your technical stack or goals") },
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text("${notes.length}/250", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
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
                        } else if (name.length > 50) {
                            nameError = "Name must be max 50 characters"
                            hasError = true
                        } else {
                            nameError = null
                        }

                        if (email.isBlank()) {
                            emailError = "Corporate email is required"
                            hasError = true
                        } else if (email.length > 50) {
                            emailError = "Email must be max 50 characters"
                            hasError = true
                        } else if (!isValidEmail(email)) {
                            emailError = "Invalid email format"
                            hasError = true
                        } else {
                            emailError = null
                        }

                        if (notes.length > 250) {
                            hasError = true
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

                        if (!hasError && proposedDate.isNotBlank() && proposedTime.isNotBlank()) {
                            val dateTimeFormat = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US).apply {
                                timeZone = java.util.TimeZone.getTimeZone("Pacific/Auckland")
                            }
                            try {
                                val selectedDateTime = dateTimeFormat.parse("$proposedDate $proposedTime")
                                val now = Date()
                                if (selectedDateTime != null && selectedDateTime.before(now)) {
                                    dateError = "Date/time cannot be in the past (NZST)"
                                    timeError = "Date/time cannot be in the past (NZST)"
                                    hasError = true
                                }
                            } catch (e: Exception) {
                                // Parsing safety fallback
                            }
                        }

                        if (!hasError) {
                            var timeMs = System.currentTimeMillis() + (24 * 3600 * 1000L)
                            val dateTimeFormat = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US).apply {
                                timeZone = java.util.TimeZone.getTimeZone("Pacific/Auckland")
                            }
                            try {
                                val selectedDateTime = dateTimeFormat.parse("$proposedDate $proposedTime")
                                if (selectedDateTime != null) {
                                    timeMs = selectedDateTime.time
                                }
                            } catch (e: Exception) {}
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
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant
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
                                Text(text = s.split(" ").first(), color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
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
                                Text(text = s.split(" ").first(), color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
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

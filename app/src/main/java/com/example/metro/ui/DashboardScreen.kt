package com.example.metro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.AssistanceType
import com.example.metro.data.model.OtherInformation
import com.example.metro.data.model.RequestStatus
import com.example.metro.data.model.Station
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MetroNavy
import com.example.ui.theme.MetroPink
import com.example.ui.theme.MetroYellowDark
import com.example.ui.theme.SuccessGreen
import com.example.metro.viewmodel.DashboardStats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentStation: Station,
    staffName: String,
    allStations: List<Station>,
    stats: DashboardStats,
    requests: List<AssistanceRequest>,
    notices: List<OtherInformation>,
    isSoundEnabled: Boolean,
    isDarkTheme: Boolean = false,
    onToggleSound: () -> Unit,
    onToggleTheme: () -> Unit = {},
    onTestAlarm: () -> Unit,
    onSwitchStation: (String) -> Unit,
    onOpenRequestDialog: (AssistanceType) -> Unit,
    onOpenBroadcastDialog: () -> Unit,
    onAcknowledgeRequest: (String) -> Unit,
    onMarkTrainDeparted: (String) -> Unit,
    onOpenLocationDialog: (AssistanceRequest) -> Unit,
    onMarkAssistanceProvided: (String) -> Unit,
    onAcknowledgeNotice: (String) -> Unit,
    onOpenAdminPanel: () -> Unit,
    onOpenDemoGuide: () -> Unit,
    onLogout: () -> Unit,
    onAdvanceTrain: (String) -> Unit = {}
) {
    var selectedFilterTab by remember { mutableIntStateOf(0) }
    var stationMenuExpanded by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    if (showLogoutConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = {
                Text(
                    text = "Confirm Station Logout",
                    fontWeight = FontWeight.Bold,
                    color = MetroNavy
                )
            },
            text = {
                Text("Are you sure you want to log out from ${currentStation.stationName} (${currentStation.stationCode})? Any active alarms will be silenced.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlarmRed),
                    modifier = Modifier.testTag("confirm_logout_action")
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val filterTabs = listOf(
        "Incoming (${requests.count { it.destinationStationCode == currentStation.stationCode && it.status != RequestStatus.COMPLETED.name }})",
        "Departing (${requests.count { it.boardingStationCode == currentStation.stationCode && it.status != RequestStatus.COMPLETED.name }})",
        "All Requests (${requests.size})",
        "Broadcast Notices (${notices.count { !it.isClosed }})"
    )

    val displayedRequests = remember(requests, selectedFilterTab, currentStation) {
        when (selectedFilterTab) {
            0 -> requests.filter { it.destinationStationCode == currentStation.stationCode }
            1 -> requests.filter { it.boardingStationCode == currentStation.stationCode }
            2 -> requests
            else -> emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // App Header & Top Navigation Bar
        Surface(
            color = MetroNavy,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & App Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MetroPink)
                        ) {
                            Icon(
                                Icons.Default.DirectionsSubway,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "METRO ASSIST",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Pink Line Coordination Console",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF472B6),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Header Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Theme Toggle
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier.testTag("toggle_theme_button")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = if (isDarkTheme) Color(0xFFFFB300) else Color.White
                            )
                        }

                        // Sound Toggle
                        IconButton(
                            onClick = onToggleSound,
                            modifier = Modifier.testTag("toggle_sound_button")
                        ) {
                            Icon(
                                imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Toggle Sound",
                                tint = if (isSoundEnabled) Color(0xFFFFB300) else Color.Gray
                            )
                        }

                        // Test Alarm Button
                        OutlinedButton(
                            onClick = onTestAlarm,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFFB300))),
                            modifier = Modifier.padding(horizontal = 4.dp).testTag("test_alarm_button")
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFB300))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Alarm", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                        }

                        // Demo Guide
                        IconButton(onClick = onOpenDemoGuide, modifier = Modifier.testTag("demo_guide_button")) {
                            Icon(Icons.Default.HelpOutline, contentDescription = "Demo Guide", tint = Color.White)
                        }

                        // Admin Portal
                        IconButton(onClick = onOpenAdminPanel, modifier = Modifier.testTag("admin_portal_button")) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = Color.White)
                        }

                        // Prominent Logout Option
                        Button(
                            onClick = { showLogoutConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C)),
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .testTag("logout_button")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Logout", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Station Bar with Instant Switcher
                Surface(
                    color = Color(0xFF133E6E),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CURRENT STATION:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF93C5FD),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${currentStation.stationName} (${currentStation.stationCode})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Staff: $staffName • Seq: +${currentStation.plusSequence} / -${currentStation.minusSequence}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        // Station Switcher for live inter-station coordination testing
                        Box {
                            Button(
                                onClick = { stationMenuExpanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MetroPink),
                                modifier = Modifier.testTag("station_switcher_btn")
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch Station", style = MaterialTheme.typography.labelSmall)
                            }

                            DropdownMenu(
                                expanded = stationMenuExpanded,
                                onDismissRequest = { stationMenuExpanded = false },
                                modifier = Modifier.heightIn(max = 280.dp)
                            ) {
                                allStations.forEach { stn ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(stn.stationName)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(stn.stationCode, color = MetroPink, fontWeight = FontWeight.Bold)
                                            }
                                        },
                                        onClick = {
                                            stationMenuExpanded = false
                                            onSwitchStation(stn.stationCode)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Body Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            // Three Large Action Cards (Section 4 Requirement)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "ASSISTANCE & NOTICE ACTIONS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LargeActionCard(
                        title = "VISUALLY\nIMPAIRED",
                        subtitle = "Assistance Form",
                        icon = Icons.Default.Visibility,
                        containerColor = MetroNavy,
                        contentColor = Color.White,
                        iconTint = Color(0xFFFFB300),
                        testTag = "visually_impaired_card",
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenRequestDialog(AssistanceType.VISUALLY_IMPAIRED) }
                    )

                    LargeActionCard(
                        title = "WHEELCHAIR\nASSISTANCE",
                        subtitle = "Assistance Form",
                        icon = Icons.Default.Accessible,
                        containerColor = MetroPink,
                        contentColor = Color.White,
                        iconTint = Color.White,
                        testTag = "wheelchair_card",
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenRequestDialog(AssistanceType.WHEELCHAIR) }
                    )

                    LargeActionCard(
                        title = "OTHER\nINFO NOTICE",
                        subtitle = "Lost & Found / Broadcast",
                        icon = Icons.Default.Campaign,
                        containerColor = Color(0xFF0F766E),
                        contentColor = Color.White,
                        iconTint = Color(0xFFFDE047),
                        testTag = "other_info_card",
                        modifier = Modifier.weight(1f),
                        onClick = onOpenBroadcastDialog
                    )
                }
            }

            // Dashboard Statistics (6 cards)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "DASHBOARD STATISTICS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard("Total Requests", "${stats.totalRequests}", Color(0xFF1E293B), Modifier.weight(1f))
                    StatCard("Pending Ack", "${stats.pendingRequests}", if (stats.pendingRequests > 0) AlarmRed else Color(0xFF64748B), Modifier.weight(1f), isAlert = stats.pendingRequests > 0)
                    StatCard("Acknowledged", "${stats.acknowledgedRequests}", Color(0xFF0369A1), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard("Approaching (3-Stn)", "${stats.trainsApproaching}", if (stats.trainsApproaching > 0) AlarmRed else Color(0xFF64748B), Modifier.weight(1f), isAlert = stats.trainsApproaching > 0)
                    StatCard("Completed", "${stats.completedRequests}", SuccessGreen, Modifier.weight(1f))
                    StatCard("Active Notices", "${stats.activeNotices}", Color(0xFFB45309), Modifier.weight(1f))
                }
            }

            // Request Filter Tabs
            item {
                Spacer(modifier = Modifier.height(18.dp))
                ScrollableTabRow(
                    selectedTabIndex = selectedFilterTab,
                    containerColor = Color.White,
                    contentColor = MetroNavy,
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    filterTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedFilterTab == index,
                            onClick = { selectedFilterTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (selectedFilterTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // List of Requests / Notices
            if (selectedFilterTab == 3) {
                // Notice Board Tab
                if (notices.isEmpty()) {
                    item {
                        EmptyNoticeCard()
                    }
                } else {
                    items(notices) { notice ->
                        NoticeCard(
                            notice = notice,
                            currentStation = currentStation,
                            onAcknowledge = { onAcknowledgeNotice(notice.id) }
                        )
                    }
                }
            } else {
                // Assistance Requests Tab
                if (displayedRequests.isEmpty()) {
                    item {
                        EmptyRequestCard(filterName = filterTabs[selectedFilterTab])
                    }
                } else {
                    items(displayedRequests) { req ->
                        RequestCard(
                            request = req,
                            currentStation = currentStation,
                            onAcknowledge = { onAcknowledgeRequest(req.id) },
                            onMarkDeparted = { onMarkTrainDeparted(req.id) },
                            onUpdateLocation = { onOpenLocationDialog(req) },
                            onAdvanceTrain = { onAdvanceTrain(req.id) },
                            onMarkCompleted = { onMarkAssistanceProvided(req.id) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun LargeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    iconTint: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .height(125.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    lineHeight = MaterialTheme.typography.titleSmall.lineHeight * 1.1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) Color(0xFFFFEBEE) else Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.border(
            width = if (isAlert) 1.5.dp else 0.dp,
            color = if (isAlert) AlarmRed else Color.Transparent,
            shape = RoundedCornerShape(10.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = valueColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RequestCard(
    request: AssistanceRequest,
    currentStation: Station,
    onAcknowledge: () -> Unit,
    onMarkDeparted: () -> Unit,
    onUpdateLocation: () -> Unit,
    onAdvanceTrain: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    val isDestinationStation = (request.destinationStationCode == currentStation.stationCode)
    val isBoardingStation = (request.boardingStationCode == currentStation.stationCode)

    val isPendingAck = (request.status == RequestStatus.PENDING_ACK.name)
    val isAcknowledged = (request.status == RequestStatus.ACKNOWLEDGED.name)
    val isDeparted = (request.status == RequestStatus.DEPARTED.name || request.status == RequestStatus.IN_TRANSIT.name)
    val isApproaching = (request.status == RequestStatus.APPROACHING_3_STATION.name)
    val isCompleted = (request.status == RequestStatus.COMPLETED.name)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isApproaching) Color(0xFFFFEBEE) else Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .border(
                width = if (isApproaching) 2.dp else 1.dp,
                color = if (isApproaching) AlarmRed else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Request ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (request.assistanceType.contains("VISUALLY", ignoreCase = true))
                            Icons.Default.Visibility else Icons.Default.Accessible,
                        contentDescription = null,
                        tint = MetroPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = request.id,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MetroNavy
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${request.passengerCount} Pax",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isCompleted -> Color(0xFFE8F5E9)
                                isApproaching -> Color(0xFFFFCDD2)
                                isAcknowledged -> Color(0xFFE0F2FE)
                                isPendingAck -> Color(0xFFFFF7ED)
                                else -> Color(0xFFF1F5F9)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (request.status) {
                            RequestStatus.PENDING_ACK.name -> "Pending Ack"
                            RequestStatus.ACKNOWLEDGED.name -> "Acknowledged"
                            RequestStatus.DEPARTED.name -> "Train Departed"
                            RequestStatus.IN_TRANSIT.name -> "In Transit"
                            RequestStatus.APPROACHING_3_STATION.name -> "⚠️ 3 Stns Away (Alert)"
                            RequestStatus.COMPLETED.name -> "Completed ✓"
                            else -> request.status
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCompleted -> SuccessGreen
                            isApproaching -> AlarmRed
                            isAcknowledged -> Color(0xFF0369A1)
                            isPendingAck -> Color(0xFFC2410C)
                            else -> Color(0xFF334155)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Route line
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${request.boardingStationName} (${request.boardingStationCode})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isBoardingStation) MetroNavy else Color(0xFF475569)
                )
                Text(
                    text = "  ➔  ",
                    color = MetroPink,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${request.destinationStationName} (${request.destinationStationCode})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDestinationStation) MetroPink else Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Train & Set info
            Text(
                text = "Train #${request.trainNumber} • Train Set #${request.trainSetNumber} • Direction: ${request.circularDirection}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )

            if (request.specialNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${request.specialNotes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF0F766E)
                )
            }

            // Train Tracking Panel (Average 03:00 min / station)
            if ((isDeparted || isApproaching) && !isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                val rem = request.currentRemainingStations ?: 0
                Surface(
                    color = if (isApproaching) Color(0xFFFFCDD2).copy(alpha = 0.5f) else Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isApproaching) AlarmRed else Color(0xFF86EFAC)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DirectionsSubway,
                                    contentDescription = null,
                                    tint = if (isApproaching) AlarmRed else SuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TRAIN TRACKING (Avg 03:00 min/stn)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isApproaching) AlarmRed else Color(0xFF166534)
                                )
                            }
                            Text(
                                text = "Next Stn: in ~03:00 min",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isApproaching) AlarmRed else Color(0xFF15803D)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Current Train Location: ${request.currentStationName ?: request.boardingStationName}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MetroNavy
                        )
                        Text(
                            text = "$rem stations remaining to destination • Total ETA: ~${rem * 3} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (rem == 3) AlarmRed else Color(0xFF475569),
                            fontWeight = if (rem == 3) FontWeight.Bold else FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAdvanceTrain,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isApproaching) AlarmRed else Color(0xFF0F766E)
                                ),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("advance_train_${request.id}")
                            ) {
                                Text(
                                    text = "⚡ Advance Next Stn (+3m)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = onUpdateLocation,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("update_location_button_${request.id}")
                            ) {
                                Text(
                                    text = "Route Tracker",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons based on station role & status
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Acknowledge Button for destination station
                if (isDestinationStation && isPendingAck) {
                    Button(
                        onClick = onAcknowledge,
                        colors = ButtonDefaults.buttonColors(containerColor = MetroPink),
                        modifier = Modifier.testTag("ack_request_button_${request.id}")
                    ) {
                        Text("ACKNOWLEDGE REQUEST")
                    }
                }

                // Train Departed button for boarding station
                if (isBoardingStation && isAcknowledged) {
                    Button(
                        onClick = onMarkDeparted,
                        colors = ButtonDefaults.buttonColors(containerColor = MetroNavy),
                        modifier = Modifier.testTag("train_departed_button_${request.id}")
                    ) {
                        Text("TRAIN DEPARTED")
                    }
                }

                // Assistance Provided (Mark Completed) button for destination station
                if (isDestinationStation && (isDeparted || isApproaching) && !isCompleted) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onMarkCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.testTag("assistance_provided_button_${request.id}")
                    ) {
                        Text("ASSISTANCE PROVIDED ✓")
                    }
                }
            }
        }
    }
}

@Composable
private fun NoticeCard(
    notice: OtherInformation,
    currentStation: Station,
    onAcknowledge: () -> Unit
) {
    val timeStr = remember(notice.timestamp) {
        SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()).format(Date(notice.timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color(0xFFFEF08A), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MetroYellowDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = notice.category,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "From: ${notice.originatingStationName} (${notice.originatingStationCode}) • By: ${notice.createdStaff}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF475569)
            )

            if (notice.locationDetails.isNotBlank()) {
                Text(
                    text = "Location: ${notice.locationDetails}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MetroNavy
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notice.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onAcknowledge) {
                    Text("Acknowledge Receipt", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun EmptyRequestCard(filterName: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.DirectionsSubway,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No Requests in $filterName",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
            Text(
                text = "Use the large cards above to create a Visually Impaired or Wheelchair request.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyNoticeCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Campaign,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No Active Broadcast Notices",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
            Text(
                text = "Broadcast Lost & Found or Passenger notices to all 38 Pink Line stations using the 'OTHER INFO NOTICE' card.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}

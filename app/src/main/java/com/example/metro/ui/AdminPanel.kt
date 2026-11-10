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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.AuditLog
import com.example.metro.data.model.RouteEngine
import com.example.metro.data.model.Station
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MetroNavy
import com.example.ui.theme.MetroPink
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanel(
    allStations: List<Station>,
    allRequests: List<AssistanceRequest>,
    auditLogs: List<AuditLog>,
    onAddOrUpdateStation: (Station) -> Unit,
    onToggleStationActive: (Station) -> Unit,
    onResetToPinkLineMaster: () -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Station Master", "Route Engine", "All Requests", "Reports", "Audit Logs")

    var showAddDialog by remember { mutableStateOf(false) }
    var stationSearch by remember { mutableStateOf("") }

    val filteredStations = remember(allStations, stationSearch) {
        if (stationSearch.isBlank()) allStations
        else allStations.filter {
            it.stationName.contains(stationSearch, ignoreCase = true) ||
                    it.stationCode.contains(stationSearch, ignoreCase = true)
        }
    }

    val routeValidation = remember(allStations) {
        RouteEngine.validateStationMaster(allStations)
    }

    val adminFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A),
        focusedContainerColor = Color(0xFFF8FAFC),
        unfocusedContainerColor = Color(0xFFF1F5F9),
        focusedBorderColor = MetroNavy,
        unfocusedBorderColor = Color(0xFF94A3B8),
        cursorColor = MetroNavy
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Bar
        Surface(
            color = MetroNavy,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Central Administrator Portal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Delhi Metro Pink Line Network Master",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF93C5FD)
                        )
                    }
                }

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text("Exit Admin")
                }
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = MetroNavy,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Station Master
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = stationSearch,
                                onValueChange = { stationSearch = it },
                                placeholder = { Text("Search by station or code...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                colors = adminFieldColors,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MetroPink)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Station")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Stations: ${allStations.size} (${allStations.count { it.isActive }} Active)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )

                            OutlinedButton(
                                onClick = onResetToPinkLineMaster,
                                modifier = Modifier.testTag("reset_master_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset 38 Pink Line Master", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredStations) { stn ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = stn.stationName,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MetroNavy
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFE2E8F0))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = stn.stationCode,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "+ Circular Seq: ${stn.plusSequence}  |  - Circular Seq: ${stn.minusSequence}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (stn.isActive) "Active" else "Inactive",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (stn.isActive) SuccessGreen else Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Switch(
                                                checked = stn.isActive,
                                                onCheckedChange = { onToggleStationActive(stn) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Route Engine Verification
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (routeValidation.isValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (routeValidation.isValid) Icons.Default.CheckCircle else Icons.Default.DirectionsSubway,
                                    contentDescription = null,
                                    tint = if (routeValidation.isValid) SuccessGreen else AlarmRed,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (routeValidation.isValid) "Route Sequences Verified" else "Validation Errors Found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (routeValidation.isValid) SuccessGreen else AlarmRed
                                    )
                                    Text(
                                        text = if (routeValidation.isValid)
                                            "All 38 Pink Line stations have unique codes, valid sequences, and dual circular mappings."
                                        else routeValidation.errors.joinToString("\n"),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "+ Circular Sequence (Majlis Park → Shiv Vihar)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MetroNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = allStations.sortedBy { it.plusSequence }
                                .joinToString(" → ") { "${it.plusSequence}. ${it.stationName}" },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF334155),
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.4
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "- Circular Sequence (Shiv Vihar → Majlis Park)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MetroNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = allStations.sortedBy { it.minusSequence }
                                .joinToString(" → ") { "${it.minusSequence}. ${it.stationName}" },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF334155),
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.4
                        )
                    }
                }

                2 -> {
                    // All Requests Monitor
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(allRequests) { req ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = req.id,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MetroNavy
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when (req.status) {
                                                        "COMPLETED" -> Color(0xFFE8F5E9)
                                                        "APPROACHING_3_STATION" -> Color(0xFFFFCDD2)
                                                        "ACKNOWLEDGED" -> Color(0xFFE0F2FE)
                                                        else -> Color(0xFFFFF7ED)
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = req.status,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = when (req.status) {
                                                    "COMPLETED" -> SuccessGreen
                                                    "APPROACHING_3_STATION" -> AlarmRed
                                                    "ACKNOWLEDGED" -> Color(0xFF0369A1)
                                                    else -> Color(0xFFC2410C)
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${req.assistanceType.replace("_", " ")} • ${req.passengerCount} Pax • Train #${req.trainNumber} (Set ${req.trainSetNumber})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${req.boardingStationName} → ${req.destinationStationName} (${req.circularDirection})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Reports & Analytics
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        val viCount = allRequests.count { it.assistanceType.contains("VISUALLY", ignoreCase = true) }
                        val wcCount = allRequests.count { it.assistanceType.contains("WHEELCHAIR", ignoreCase = true) }
                        val totalPax = allRequests.sumOf { it.passengerCount }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetricCard(title = "Total Assistance Req", value = "${allRequests.size}", modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            MetricCard(title = "Total Passengers", value = "$totalPax", modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetricCard(title = "Visually Impaired", value = "$viCount", modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            MetricCard(title = "Wheelchair", value = "$wcCount", modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Station-wise Assistance Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetroNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val stationCounts = allRequests.groupBy { it.destinationStationName }
                            .mapValues { it.value.size }
                            .toList()
                            .sortedByDescending { it.second }

                        if (stationCounts.isEmpty()) {
                            Text("No request data yet. Create requests to see station-wise analytics.", color = Color.Gray)
                        } else {
                            stationCounts.forEach { (stationName, count) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(stationName, style = MaterialTheme.typography.bodyMedium)
                                    Text("$count requests", fontWeight = FontWeight.Bold, color = MetroPink)
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // Audit Logs
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(auditLogs) { log ->
                            val timeStr = remember(log.timestamp) {
                                SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = log.action,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MetroNavy
                                        )
                                        Text(
                                            text = timeStr,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = log.details,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF334155)
                                    )
                                    Text(
                                        text = "By: ${log.performedBy} • Station: ${log.stationCode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Station Dialog
    if (showAddDialog) {
        var newCode by remember { mutableStateOf("") }
        var newName by remember { mutableStateOf("") }
        var plusSeq by remember { mutableStateOf("${allStations.size + 1}") }
        var minusSeq by remember { mutableStateOf("1") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Add Station to Master",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetroNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newCode,
                        onValueChange = { newCode = it.uppercase() },
                        label = { Text("Station Code (e.g. DLCT)") },
                        colors = adminFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Station Name") },
                        colors = adminFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = plusSeq,
                        onValueChange = { plusSeq = it },
                        label = { Text("+ Circular Seq Number") },
                        colors = adminFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = minusSeq,
                        onValueChange = { minusSeq = it },
                        label = { Text("- Circular Seq Number") },
                        colors = adminFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { showAddDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newCode.isNotBlank() && newName.isNotBlank()) {
                                    onAddOrUpdateStation(
                                        Station(
                                            stationCode = newCode.trim(),
                                            stationName = newName.trim(),
                                            plusSequence = plusSeq.toIntOrNull() ?: 1,
                                            minusSequence = minusSeq.toIntOrNull() ?: 1,
                                            isActive = true
                                        )
                                    )
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MetroNavy)
                        ) {
                            Text("Save Station")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MetroNavy)
        }
    }
}

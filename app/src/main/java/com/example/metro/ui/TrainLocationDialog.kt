package com.example.metro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.metro.data.model.RouteEngine
import com.example.metro.data.model.Station
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MetroNavy
import com.example.ui.theme.MetroPink
import com.example.ui.theme.SuccessGreen

@Composable
fun TrainLocationDialog(
    request: AssistanceRequest,
    allStations: List<Station>,
    onDismiss: () -> Unit,
    onLocationSelected: (stationCode: String) -> Unit
) {
    val route = remember(request, allStations) {
        RouteEngine.calculateRoute(
            allStations = allStations,
            boardingCode = request.boardingStationCode,
            destinationCode = request.destinationStationCode,
            direction = request.circularDirection
        )
    }

    // Determine current index in route
    val currentRouteIdx = remember(route, request.currentStationCode) {
        val idx = route.indexOfFirst { it.stationCode == request.currentStationCode }
        if (idx == -1) 0 else idx
    }

    var selectedStationCode by remember {
        // Pre-select next progressive station if available
        val nextIdx = (currentRouteIdx + 1).coerceAtMost(route.size - 1)
        mutableStateOf(if (route.isNotEmpty()) route[nextIdx].stationCode else "")
    }

    val selectedRouteIdx = remember(route, selectedStationCode) {
        route.indexOfFirst { it.stationCode == selectedStationCode }
    }

    val remainingFromSelected = remember(route, selectedRouteIdx) {
        if (selectedRouteIdx >= 0) (route.size - 1) - selectedRouteIdx else 0
    }

    val isThreeStationWarning = (remainingFromSelected == 3)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Update Train Location",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MetroNavy
                        )
                        Text(
                            text = "Train #${request.trainNumber} (Set ${request.trainSetNumber}) • ${request.circularDirection}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3-Minute Average Train Tracking Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DirectionsSubway,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TRAIN TRACKING (AVERAGE 03:00 MIN / STATION)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                text = "Train reaches each subsequent station in ~3 minutes. Destination ETA is dynamically computed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                }

                // Alert when 3 stations away
                if (isThreeStationWarning) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AlarmRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "CRITICAL 3-STATION THRESHOLD",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AlarmRed
                                )
                                Text(
                                    text = "Selecting this station triggers the automatic 2nd Audible Alarm at ${request.destinationStationName} (ETA ~9 min)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AlarmRed
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Next Station (Forward Progression Only):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Route Station List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                ) {
                    itemsIndexed(route) { index, station ->
                        val isCurrent = (index == currentRouteIdx)
                        val isSelected = (station.stationCode == selectedStationCode)
                        val isPast = (index < currentRouteIdx)
                        val isDestination = (index == route.size - 1)
                        val remaining = (route.size - 1) - index

                        val isSelectable = index > currentRouteIdx

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isSelected && remaining == 3 -> Color(0xFFFFCDD2)
                                    isSelected -> MaterialTheme.colorScheme.secondaryContainer
                                    isCurrent -> Color(0xFFE8F5E9)
                                    isPast -> Color(0xFFF1F5F9)
                                    else -> Color.White
                                }
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MetroPink else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = isSelectable) {
                                    selectedStationCode = station.stationCode
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Station badge
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent -> SuccessGreen
                                                isDestination -> MetroNavy
                                                remaining == 3 -> AlarmRed
                                                else -> Color(0xFF94A3B8)
                                            }
                                        )
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = station.stationName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isCurrent || isSelected || isDestination)
                                                FontWeight.Bold else FontWeight.Normal,
                                            color = if (isPast) Color.Gray else MetroNavy
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "[Current]",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (isDestination) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "[Destination]",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MetroPink,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (remaining == 0) "At Destination"
                                        else "$remaining stations away • ETA ~${remaining * 3} min",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (remaining == 3) AlarmRed else Color(0xFF64748B)
                                    )
                                }

                                if (isSelectable) {
                                    Text(
                                        text = if (isSelected) "Selected" else "Select",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MetroPink else Color(0xFF0284C7),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom summary & update button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Remaining: $remainingFromSelected stn",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingFromSelected == 3) AlarmRed else MetroNavy
                        )
                        Text(
                            text = "ETA: ~${remainingFromSelected * 3} mins",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }

                    Row {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (selectedStationCode.isNotBlank()) {
                                    onLocationSelected(selectedStationCode)
                                }
                            },
                            enabled = selectedRouteIdx > currentRouteIdx,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isThreeStationWarning) AlarmRed else MetroNavy
                            ),
                            modifier = Modifier.testTag("confirm_location_update")
                        ) {
                            Text("Update Location")
                        }
                    }
                }
            }
        }
    }
}

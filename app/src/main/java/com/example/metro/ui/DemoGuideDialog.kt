package com.example.metro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.Station
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MetroNavy
import com.example.ui.theme.MetroPink
import com.example.ui.theme.SuccessGreen

@Composable
fun DemoGuideDialog(
    currentStation: Station?,
    requests: List<AssistanceRequest>,
    onSwitchStation: (String) -> Unit,
    onAutoCreateDemoRequest: () -> Unit,
    onAdvanceTrainTo3StationsAway: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val demoReq = requests.find {
        (it.boardingStationCode == "AVIS" && it.destinationStationCode == "LJPN") ||
                it.trainNumber == "2890"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = MetroNavy,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guided Demo Scenario",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MetroNavy
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Delhi Metro Pink Line Coordination: Anand Vihar ↔ Lajpat Nagar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MetroPink,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Current Context Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Active Station Session:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = currentStation?.let { "${it.stationName} (${it.stationCode})" } ?: "None",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MetroNavy
                            )
                        }

                        // Quick Switch Button
                        OutlinedButton(
                            onClick = {
                                if (currentStation?.stationCode == "AVIS") {
                                    onSwitchStation("LJPN")
                                } else {
                                    onSwitchStation("AVIS")
                                }
                            },
                            modifier = Modifier.testTag("switch_station_demo_btn")
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentStation?.stationCode == "AVIS") "Switch to Lajpat Nagar" else "Switch to Anand Vihar",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Steps list
                StepCard(
                    stepNum = "1",
                    title = "Boarding Station: Anand Vihar",
                    desc = "Create a Visually Impaired request for Lajpat Nagar (Train: 2890, Set: 23, 2 Pax).",
                    isDone = demoReq != null,
                    actionButton = if (demoReq == null) {
                        {
                            Button(
                                onClick = onAutoCreateDemoRequest,
                                colors = ButtonDefaults.buttonColors(containerColor = MetroPink),
                                modifier = Modifier.fillMaxWidth().testTag("auto_create_demo_req")
                            ) {
                                Text("Auto-Create Demo Request", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                StepCard(
                    stepNum = "2",
                    title = "Destination Station: Lajpat Nagar Alert",
                    desc = "Switch to Lajpat Nagar to hear the incoming audible alarm & acknowledge the request.",
                    isDone = demoReq?.acknowledgedAt != null,
                    actionButton = if (demoReq != null && demoReq.acknowledgedAt == null) {
                        {
                            Button(
                                onClick = { onSwitchStation("LJPN") },
                                colors = ButtonDefaults.buttonColors(containerColor = MetroNavy),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Switch to Lajpat Nagar to Ack", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                StepCard(
                    stepNum = "3",
                    title = "Train Departure & Tracking",
                    desc = "At Anand Vihar, mark 'Train Departed' and update location station-by-station.",
                    isDone = demoReq?.departedAt != null
                )

                Spacer(modifier = Modifier.height(8.dp))

                StepCard(
                    stepNum = "4",
                    title = "Critical 3-Station Second Alarm!",
                    desc = "When train reaches Sarai Kale Khan (3 stations before Lajpat Nagar), a second flashing red alarm triggers with ETA 9 mins!",
                    isDone = demoReq?.secondAlarmTriggered == true,
                    actionButton = if (demoReq != null && demoReq.departedAt != null && !demoReq.secondAlarmTriggered) {
                        {
                            Button(
                                onClick = { onAdvanceTrainTo3StationsAway(demoReq.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = AlarmRed),
                                modifier = Modifier.fillMaxWidth().testTag("advance_to_3_stations_btn")
                            ) {
                                Text("Simulate Train at 3-Stns (Trigger 2nd Alarm)", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                StepCard(
                    stepNum = "5",
                    title = "Assistance Provided & Completion",
                    desc = "At Lajpat Nagar, click 'Assistance Provided' to mark the request COMPLETED on all station consoles.",
                    isDone = demoReq?.status == "COMPLETED"
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MetroNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got It! Close Guide")
                }
            }
        }
    }
}

@Composable
private fun StepCard(
    stepNum: String,
    title: String,
    desc: String,
    isDone: Boolean,
    actionButton: (@Composable () -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFE8F5E9) else Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isDone) SuccessGreen else MetroNavy)
                ) {
                    Text(
                        text = if (isDone) "✓" else stepNum,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDone) SuccessGreen else MetroNavy
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF475569)
            )

            if (actionButton != null) {
                Spacer(modifier = Modifier.height(8.dp))
                actionButton()
            }
        }
    }
}

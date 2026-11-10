package com.example.metro.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.OtherInformation
import com.example.metro.data.model.RequestStatus
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MetroNavy
import com.example.ui.theme.MetroPink
import com.example.ui.theme.MetroYellowDark

@Composable
fun AlarmOverlay(
    request: AssistanceRequest?,
    broadcastNotice: OtherInformation?,
    onAcknowledgeRequest: (String) -> Unit,
    onAcknowledgeSecondAlarm: (String) -> Unit,
    onAcknowledgeNotice: (String) -> Unit,
    onSilenceAlarm: () -> Unit
) {
    if (request == null && broadcastNotice == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseBorderColor by infiniteTransition.animateColor(
        initialValue = AlarmRed,
        targetValue = Color.Yellow,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderColor"
    )

    val pulseBgColor by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFEBEE),
        targetValue = Color(0xFFFFCDD2),
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgColor"
    )

    Dialog(
        onDismissRequest = onSilenceAlarm,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        if (request != null) {
            val isSecondAlarm = (request.status == RequestStatus.APPROACHING_3_STATION.name)

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (isSecondAlarm) pulseBgColor else MaterialTheme.colorScheme.surface,
                tonalElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSecondAlarm) 4.dp else 2.dp,
                        color = if (isSecondAlarm) pulseBorderColor else MetroPink,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Header Icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isSecondAlarm) AlarmRed else MetroPink)
                    ) {
                        Icon(
                            imageVector = if (isSecondAlarm) Icons.Default.Warning else Icons.Default.NotificationsActive,
                            contentDescription = "Alert",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSecondAlarm) {
                        Text(
                            text = "TRAIN APPROACHING – PLEASE PREPARE FOR DIVYANGJAN ASSISTANCE.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = AlarmRed,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Train is exactly 3 stations before destination • ETA ~9 Minutes",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7F1D1D),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "INCOMING ASSISTANCE REQUEST",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MetroNavy,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Immediate Station Coordination Required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MetroPink,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Request Details Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            DetailRow("Request ID", request.id)
                            DetailRow("Assistance Type", request.assistanceType.replace("_", " "))
                            DetailRow("Passengers", "${request.passengerCount} Divyangjan")
                            DetailRow("Train / Set", "Train #${request.trainNumber} • Set #${request.trainSetNumber}")
                            DetailRow("Boarding Station", "${request.boardingStationName} (${request.boardingStationCode})")
                            DetailRow("Destination Station", "${request.destinationStationName} (${request.destinationStationCode})")
                            DetailRow("Direction", request.circularDirection)
                            if (request.currentStationName != null) {
                                DetailRow("Current Train Location", request.currentStationName)
                            }
                            if (request.specialNotes.isNotBlank()) {
                                DetailRow("Special Notes", request.specialNotes)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSilenceAlarm,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("silence_alarm_button")
                        ) {
                            Icon(Icons.Default.VolumeOff, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Silence Alarm")
                        }

                        Button(
                            onClick = {
                                if (isSecondAlarm) {
                                    onAcknowledgeSecondAlarm(request.id)
                                } else {
                                    onAcknowledgeRequest(request.id)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSecondAlarm) AlarmRed else MetroPink
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("acknowledge_alarm_button")
                        ) {
                            Text("ACKNOWLEDGE", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else if (broadcastNotice != null) {
            // Broadcast Notice Popup
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, MetroYellowDark, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MetroYellowDark)
                    ) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "NETWORK BROADCAST ALERT",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MetroNavy
                    )
                    Text(
                        text = broadcastNotice.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = MetroYellowDark,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            DetailRow("Originating Station", "${broadcastNotice.originatingStationName} (${broadcastNotice.originatingStationCode})")
                            DetailRow("Reported By", broadcastNotice.createdStaff)
                            if (broadcastNotice.locationDetails.isNotBlank()) {
                                DetailRow("Location Details", broadcastNotice.locationDetails)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Description:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = broadcastNotice.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSilenceAlarm,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dismiss")
                        }

                        Button(
                            onClick = { onAcknowledgeNotice(broadcastNotice.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MetroNavy),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("ack_broadcast_button")
                        ) {
                            Text("Acknowledge Receipt")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

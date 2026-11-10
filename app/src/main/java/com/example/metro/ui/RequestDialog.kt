package com.example.metro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.metro.data.model.AssistanceType
import com.example.metro.data.model.Station
import com.example.ui.theme.MetroNavy
import com.example.ui.theme.MetroPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDialog(
    initialType: AssistanceType,
    currentStation: Station,
    activeStations: List<Station>,
    onDismiss: () -> Unit,
    onSubmit: (
        type: String,
        destCode: String,
        passengers: Int,
        trainNum: String,
        trainSet: String,
        direction: String,
        notes: String
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    val candidateDestinations = remember(activeStations, currentStation) {
        activeStations.filter { it.stationCode != currentStation.stationCode }
    }

    var destExpanded by remember { mutableStateOf(false) }
    var selectedDest by remember {
        mutableStateOf(
            // If current is Anand Vihar (AVIS), default destination to Lajpat Nagar (LJPN) for demo convenience!
            if (currentStation.stationCode == "AVIS") {
                candidateDestinations.find { it.stationCode == "LJPN" } ?: candidateDestinations.firstOrNull()
            } else {
                candidateDestinations.firstOrNull()
            }
        )
    }

    var passengerCount by remember { mutableIntStateOf(2) }
    var trainNumber by remember { mutableStateOf("2890") }
    var trainSetNumber by remember { mutableStateOf("23") }
    var circularDirection by remember {
        mutableStateOf(
            if (currentStation.stationCode == "AVIS" && selectedDest?.stationCode == "LJPN") "- Circular"
            else "+ Circular"
        )
    }
    var specialNotes by remember { mutableStateOf("Coach 2 near Divyangjan dedicated area") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A),
        focusedContainerColor = Color(0xFFF8FAFC),
        unfocusedContainerColor = Color(0xFFF1F5F9),
        focusedBorderColor = MetroPink,
        unfocusedBorderColor = Color(0xFF94A3B8),
        cursorColor = MetroPink
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
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
                            imageVector = if (selectedType == AssistanceType.VISUALLY_IMPAIRED)
                                Icons.Default.Visibility else Icons.Default.Accessible,
                            contentDescription = null,
                            tint = MetroPink
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "New Assistance Request",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MetroNavy
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Assistance Type Selector
                Text(
                    text = "1. Assistance Type",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        onClick = { selectedType = AssistanceType.VISUALLY_IMPAIRED },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedType == AssistanceType.VISUALLY_IMPAIRED)
                                MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == AssistanceType.VISUALLY_IMPAIRED,
                                onClick = { selectedType = AssistanceType.VISUALLY_IMPAIRED }
                            )
                            Text(
                                text = "Visually Impaired",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        onClick = { selectedType = AssistanceType.WHEELCHAIR },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedType == AssistanceType.WHEELCHAIR)
                                MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == AssistanceType.WHEELCHAIR,
                                onClick = { selectedType = AssistanceType.WHEELCHAIR }
                            )
                            Text(
                                text = "Wheelchair",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Boarding Station (Locked)
                Text(
                    text = "2. Boarding Station (Auto-selected)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = "${currentStation.stationName} (${currentStation.stationCode})",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    colors = fieldColors,
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsSubway, contentDescription = null, tint = MetroNavy)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Destination Station Dropdown
                Text(
                    text = "3. Assistance Required At (Destination Station)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = destExpanded,
                    onExpandedChange = { destExpanded = !destExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDest?.let { "${it.stationName} (${it.stationCode})" } ?: "Select Station",
                        onValueChange = {},
                        readOnly = true,
                        colors = fieldColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("destination_station_input")
                    )
                    ExposedDropdownMenu(
                        expanded = destExpanded,
                        onDismissRequest = { destExpanded = false }
                    ) {
                        candidateDestinations.forEach { stn ->
                            DropdownMenuItem(
                                text = { Text("${stn.stationName} (${stn.stationCode})") },
                                onClick = {
                                    selectedDest = stn
                                    destExpanded = false
                                    // Auto-suggest circular direction
                                    if (currentStation.plusSequence < stn.plusSequence) {
                                        circularDirection = "+ Circular"
                                    } else {
                                        circularDirection = "- Circular"
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Passenger Count & Train Info
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "4. Passengers",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { if (passengerCount > 1) passengerCount-- },
                                modifier = Modifier.width(48.dp)
                            ) { Text("-") }
                            Text(
                                text = "$passengerCount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            OutlinedButton(
                                onClick = { if (passengerCount < 10) passengerCount++ },
                                modifier = Modifier.width(48.dp)
                            ) { Text("+") }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "5. Train Number",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = trainNumber,
                            onValueChange = { trainNumber = it },
                            placeholder = { Text("2890") },
                            colors = fieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("train_number_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "6. Train Set Number",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = trainSetNumber,
                            onValueChange = { trainSetNumber = it },
                            placeholder = { Text("23") },
                            colors = fieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("train_set_input")
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            text = "7. Circular Direction",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = circularDirection == "+ Circular",
                                onClick = { circularDirection = "+ Circular" }
                            )
                            Text("+ Circ", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.width(4.dp))
                            RadioButton(
                                selected = circularDirection == "- Circular",
                                onClick = { circularDirection = "- Circular" }
                            )
                            Text("- Circ", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Special Notes / Platform / Coach",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = specialNotes,
                    onValueChange = { specialNotes = it },
                    placeholder = { Text("e.g. Coach 2 near Divyangjan space") },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            selectedDest?.let { dest ->
                                onSubmit(
                                    selectedType.name,
                                    dest.stationCode,
                                    passengerCount,
                                    trainNumber,
                                    trainSetNumber,
                                    circularDirection,
                                    specialNotes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MetroPink),
                        modifier = Modifier.testTag("create_request_button")
                    ) {
                        Text("Create Request & Alert")
                    }
                }
            }
        }
    }
}

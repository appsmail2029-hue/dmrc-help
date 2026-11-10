package com.example.metro.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metro.data.model.Station
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MetroNavy
import com.example.ui.theme.MetroPink
import com.example.ui.theme.MetroYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    stations: List<Station>,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onLogin: (stationCode: String, staffName: String, pass: String) -> Boolean,
    onAdminLogin: (pass: String) -> Boolean,
    onQuickStartDemo: (stationCode: String) -> Unit
) {
    val effectiveStations = remember(stations) {
        if (stations.isNotEmpty()) stations else com.example.metro.data.model.StationSeedData.PINK_LINE_STATIONS
    }
    var selectedStation by remember(effectiveStations) {
        mutableStateOf(effectiveStations.find { it.stationCode == "AVIS" } ?: effectiveStations.first())
    }
    var staffName by remember { mutableStateOf("Duty Officer (P-104)") }
    var password by remember { mutableStateOf("12345") }
    // Default showPassword to true so characters are visibly displayed immediately
    var showPassword by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var isAdminLoginMode by remember { mutableStateOf(false) }

    // Explicit high-contrast text field colors ensuring characters are 100% visible
    val loginFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
        unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
        focusedContainerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF8FAFC),
        unfocusedContainerColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9),
        focusedBorderColor = MetroPink,
        unfocusedBorderColor = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
        cursorColor = MetroPink,
        focusedLeadingIconColor = MetroPink,
        unfocusedLeadingIconColor = if (isDarkTheme) Color(0xFF94A3B8) else MetroNavy,
        focusedTrailingIconColor = MetroPink,
        unfocusedTrailingIconColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
        focusedPlaceholderColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
        unfocusedPlaceholderColor = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (isDarkTheme) Color(0xFF0B132B) else Color(0xFFF1F5F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Controls Bar: Theme Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onToggleTheme,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDarkTheme) Color.White else MetroNavy
                    ),
                    modifier = Modifier.testTag("login_theme_toggle")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Theme",
                        modifier = Modifier.size(16.dp),
                        tint = if (isDarkTheme) Color(0xFFFFB300) else MetroNavy
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDarkTheme) "Light Theme" else "Dark Theme",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Header Logo & Branding
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color(0xFF1E293B) else MetroNavy)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsSubway,
                    contentDescription = "Metro",
                    tint = MetroPink,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "METRO ASSIST",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (isDarkTheme) Color.White else MetroNavy,
                letterSpacing = 1.2.sp
            )

            Text(
                text = "Divyangjan Assistance & Station Coordination System",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MetroPink,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Delhi Metro Pink Line Inter-Station Network",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (isAdminLoginMode) "Central Administrator Login" else "Station Console Login",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else MetroNavy
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isAdminLoginMode) {
                        // Station Selector Dropdown
                        Text(
                            text = "Assigned Metro Station",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedStation?.let { "${it.stationName} (${it.stationCode})" } ?: "Select Station",
                                onValueChange = {},
                                readOnly = true,
                                colors = loginFieldColors,
                                leadingIcon = {
                                    Icon(Icons.Default.DirectionsSubway, contentDescription = null, tint = MetroPink)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("station_dropdown_input")
                            )

                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                effectiveStations.forEach { stn ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = stn.stationName,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = stn.stationCode,
                                                    color = MetroPink,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedStation = stn
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Staff Name / ID
                        Text(
                            text = "Station Staff / Duty Officer",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = staffName,
                            onValueChange = { staffName = it },
                            placeholder = { Text("Duty Officer") },
                            colors = loginFieldColors,
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = MetroPink)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("staff_name_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Password
                    Text(
                        text = if (isAdminLoginMode) "Admin Security Password" else "Station Security Password",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        placeholder = { Text("Initial password: 12345") },
                        colors = loginFieldColors,
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MetroPink)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF64748B)
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    Text(
                        text = "Demo prototype default password: 12345 (Pre-filled)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = AlarmRed,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Button
                    Button(
                        onClick = {
                            val passToUse = password.ifBlank { "12345" }
                            if (isAdminLoginMode) {
                                val ok = onAdminLogin(passToUse)
                                if (!ok) errorMessage = "Invalid Admin Password! Use 12345."
                            } else {
                                val targetStn = selectedStation ?: effectiveStations.find { it.stationCode == "AVIS" }
                                ?: effectiveStations.first()
                                val ok = onLogin(targetStn.stationCode, staffName.ifBlank { "Duty Officer" }, passToUse)
                                if (!ok) errorMessage = "Invalid Password! Use 12345."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdminLoginMode) (if (isDarkTheme) Color(0xFF3B82F6) else MetroNavy) else MetroPink
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        Text(
                            text = if (isAdminLoginMode) "LOGIN AS ADMINISTRATOR" else "LOGIN TO STATION CONSOLE",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggle between Station and Admin
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                isAdminLoginMode = !isAdminLoginMode
                                errorMessage = null
                            }
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAdminLoginMode) "Switch to Station Login" else "Admin Portal Access",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo 1-Click Launchers
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFFFF8E1)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rapid Demo Station Quick-Launch",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFFFDE047) else Color(0xFF92400E)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Instant 1-click login for the Anand Vihar ↔ Lajpat Nagar scenario:",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF78350F)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onQuickStartDemo("AVIS") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkTheme) Color(0xFF334155) else MetroNavy
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_anand_vihar_btn")
                        ) {
                            Text("1. Anand Vihar", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = { onQuickStartDemo("LJPN") },
                            colors = ButtonDefaults.buttonColors(containerColor = MetroPink),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_lajpat_nagar_btn")
                        ) {
                            Text("2. Lajpat Nagar", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Independent Pink Line Station Prototype • Not affiliated with official DMRC systems",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}

package com.example.metro.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.AssistanceType
import com.example.metro.viewmodel.MetroViewModel

@Composable
fun MetroApp(viewModel: MetroViewModel = viewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val staffName by viewModel.staffName.collectAsState()
    val allStations by viewModel.allStations.collectAsState()
    val activeStations by viewModel.activeStations.collectAsState()
    val requests by viewModel.allRequests.collectAsState()
    val notices by viewModel.allNotices.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val activeAlarmRequest by viewModel.activeAlarmRequest.collectAsState()
    val activeBroadcastNotice by viewModel.activeBroadcastNotice.collectAsState()

    // Dialog states
    var activeRequestDialogType by remember { mutableStateOf<AssistanceType?>(null) }
    var locationTrackingRequest by remember { mutableStateOf<AssistanceRequest?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showDemoGuide by remember { mutableStateOf(false) }
    var showAdminPanel by remember { mutableStateOf(false) }

    // Hardware back press handling
    BackHandler(enabled = showAdminPanel || showDemoGuide || activeRequestDialogType != null || locationTrackingRequest != null || showBroadcastDialog) {
        when {
            showAdminPanel -> showAdminPanel = false
            showDemoGuide -> showDemoGuide = false
            activeRequestDialogType != null -> activeRequestDialogType = null
            locationTrackingRequest != null -> locationTrackingRequest = null
            showBroadcastDialog -> showBroadcastDialog = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isLoggedIn || currentStation == null) {
            LoginScreen(
                stations = if (activeStations.isNotEmpty()) activeStations
                else com.example.metro.data.model.StationSeedData.PINK_LINE_STATIONS,
                isDarkTheme = isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme() },
                onLogin = { code, staff, pass ->
                    viewModel.login(code, staff, pass)
                },
                onAdminLogin = { pass ->
                    viewModel.adminLogin(pass)
                },
                onQuickStartDemo = { code ->
                    viewModel.login(code, "Duty Officer", "12345")
                }
            )
        } else if (isAdminMode || showAdminPanel) {
            AdminPanel(
                allStations = allStations,
                allRequests = requests,
                auditLogs = auditLogs,
                onAddOrUpdateStation = { viewModel.addOrUpdateStation(it) },
                onToggleStationActive = { viewModel.toggleStationActive(it) },
                onResetToPinkLineMaster = { viewModel.resetToPinkLineMaster() },
                onClose = {
                    if (isAdminMode) {
                        viewModel.logout()
                    } else {
                        showAdminPanel = false
                    }
                }
            )
        } else {
            DashboardScreen(
                currentStation = currentStation!!,
                staffName = staffName,
                allStations = if (activeStations.isNotEmpty()) activeStations else allStations,
                stats = stats,
                requests = requests,
                notices = notices,
                isSoundEnabled = isSoundEnabled,
                isDarkTheme = isDarkTheme,
                onToggleSound = { viewModel.toggleSound() },
                onToggleTheme = { viewModel.toggleTheme() },
                onTestAlarm = { viewModel.testAlarm() },
                onSwitchStation = { code -> viewModel.switchStation(code) },
                onOpenRequestDialog = { type -> activeRequestDialogType = type },
                onOpenBroadcastDialog = { showBroadcastDialog = true },
                onAcknowledgeRequest = { reqId -> viewModel.acknowledgeRequest(reqId) },
                onMarkTrainDeparted = { reqId -> viewModel.markTrainDeparted(reqId) },
                onOpenLocationDialog = { req -> locationTrackingRequest = req },
                onMarkAssistanceProvided = { reqId -> viewModel.markAssistanceProvided(reqId) },
                onAcknowledgeNotice = { noticeId -> viewModel.acknowledgeNotice(noticeId) },
                onOpenAdminPanel = { showAdminPanel = true },
                onOpenDemoGuide = { showDemoGuide = true },
                onLogout = { viewModel.logout() },
                onAdvanceTrain = { reqId -> viewModel.advanceTrainToNextStation(reqId) }
            )
        }

        // Active Alarms & Alerts Overlay
        if (activeAlarmRequest != null || activeBroadcastNotice != null) {
            AlarmOverlay(
                request = activeAlarmRequest,
                broadcastNotice = activeBroadcastNotice,
                onAcknowledgeRequest = { reqId -> viewModel.acknowledgeRequest(reqId) },
                onAcknowledgeSecondAlarm = { reqId -> viewModel.acknowledgeSecondAlarm(reqId) },
                onAcknowledgeNotice = { noticeId -> viewModel.acknowledgeNotice(noticeId) },
                onSilenceAlarm = { viewModel.silenceAlarm() }
            )
        }

        // Request Creation Dialog
        if (activeRequestDialogType != null && currentStation != null) {
            RequestDialog(
                initialType = activeRequestDialogType!!,
                currentStation = currentStation!!,
                activeStations = if (activeStations.isNotEmpty()) activeStations else allStations,
                onDismiss = { activeRequestDialogType = null },
                onSubmit = { type, destCode, pax, trainNum, trainSet, dir, notes ->
                    viewModel.createAssistanceRequest(
                        assistanceType = type,
                        destinationStationCode = destCode,
                        passengerCount = pax,
                        trainNumber = trainNum,
                        trainSetNumber = trainSet,
                        circularDirection = dir,
                        specialNotes = notes
                    )
                    activeRequestDialogType = null
                }
            )
        }

        // Train Location Update Dialog
        if (locationTrackingRequest != null) {
            TrainLocationDialog(
                request = locationTrackingRequest!!,
                allStations = allStations,
                onDismiss = { locationTrackingRequest = null },
                onLocationSelected = { nextStationCode ->
                    viewModel.updateTrainLocation(locationTrackingRequest!!.id, nextStationCode)
                    locationTrackingRequest = null
                }
            )
        }

        // Other Information Broadcast Dialog
        if (showBroadcastDialog && currentStation != null) {
            OtherInfoDialog(
                currentStation = currentStation!!,
                onDismiss = { showBroadcastDialog = false },
                onSubmit = { cat, desc, loc ->
                    viewModel.createBroadcastNotice(cat, desc, loc)
                    showBroadcastDialog = false
                }
            )
        }

        // Guided Demo Dialog
        if (showDemoGuide) {
            DemoGuideDialog(
                currentStation = currentStation,
                requests = requests,
                onSwitchStation = { code -> viewModel.switchStation(code) },
                onAutoCreateDemoRequest = {
                    // Auto-generate Anand Vihar -> Lajpat Nagar demo request as specified in prompt
                    viewModel.createAssistanceRequest(
                        assistanceType = AssistanceType.VISUALLY_IMPAIRED.name,
                        destinationStationCode = "LJPN",
                        passengerCount = 2,
                        trainNumber = "2890",
                        trainSetNumber = "23",
                        circularDirection = "- Circular",
                        specialNotes = "Anand Vihar demo test: 2 Visually Impaired passengers"
                    )
                },
                onAdvanceTrainTo3StationsAway = { reqId ->
                    // Sarai Kale Khan - Nizamuddin (SKKN) is 3 stations before Lajpat Nagar (LJPN)
                    viewModel.updateTrainLocation(reqId, "SKKN")
                },
                onDismiss = { showDemoGuide = false }
            )
        }
    }
}

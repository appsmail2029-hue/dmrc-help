package com.example.metro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.metro.data.local.MetroDatabase
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.AuditLog
import com.example.metro.data.model.InformationAcknowledgement
import com.example.metro.data.model.OtherInformation
import com.example.metro.data.model.RequestStatus
import com.example.metro.data.model.RouteEngine
import com.example.metro.data.model.Station
import com.example.metro.data.model.StationSeedData
import com.example.metro.data.model.TrainMovement
import com.example.metro.sound.AlarmSoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class DashboardStats(
    val totalRequests: Int = 0,
    val pendingRequests: Int = 0,
    val acknowledgedRequests: Int = 0,
    val trainsApproaching: Int = 0,
    val completedRequests: Int = 0,
    val activeNotices: Int = 0
)

class MetroViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MetroDatabase.getInstance(application)
    private val stationDao = db.stationDao()
    private val requestDao = db.assistanceRequestDao()
    private val otherInfoDao = db.otherInformationDao()
    private val auditDao = db.auditLogDao()

    val soundManager = AlarmSoundManager(application)

    // Current Session State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _currentStation = MutableStateFlow<Station?>(null)
    val currentStation: StateFlow<Station?> = _currentStation.asStateFlow()

    private val _staffName = MutableStateFlow("Duty Officer (P-104)")
    val staffName: StateFlow<String> = _staffName.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Active Alarms & Alerts
    private val _activeAlarmRequest = MutableStateFlow<AssistanceRequest?>(null)
    val activeAlarmRequest: StateFlow<AssistanceRequest?> = _activeAlarmRequest.asStateFlow()

    private val _activeBroadcastNotice = MutableStateFlow<OtherInformation?>(null)
    val activeBroadcastNotice: StateFlow<OtherInformation?> = _activeBroadcastNotice.asStateFlow()

    private val dismissedNoticeIds = mutableSetOf<String>()

    // Reactive DB streams
    val allStations: StateFlow<List<Station>> = stationDao.getAllStationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStations: StateFlow<List<Station>> = stationDao.getActiveStationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<AssistanceRequest>> = requestDao.getAllRequestsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotices: StateFlow<List<OtherInformation>> = otherInfoDao.getAllNoticesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = auditDao.getAllLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics
    val stats: StateFlow<DashboardStats> = combine(allRequests, allNotices) { reqs, notes ->
        DashboardStats(
            totalRequests = reqs.size,
            pendingRequests = reqs.count { it.status == RequestStatus.PENDING_ACK.name },
            acknowledgedRequests = reqs.count { it.status == RequestStatus.ACKNOWLEDGED.name || it.status == RequestStatus.DEPARTED.name || it.status == RequestStatus.IN_TRANSIT.name },
            trainsApproaching = reqs.count { it.status == RequestStatus.APPROACHING_3_STATION.name || it.currentRemainingStations == 3 },
            completedRequests = reqs.count { it.status == RequestStatus.COMPLETED.name },
            activeNotices = notes.count { !it.isClosed }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    init {
        // Ensure stations are seeded
        viewModelScope.launch(Dispatchers.IO) {
            if (stationDao.getStationCount() == 0) {
                stationDao.insertStations(StationSeedData.PINK_LINE_STATIONS)
            }
        }

        // Monitor incoming requests and 3-station alerts for the destination station
        viewModelScope.launch {
            combine(allRequests, _currentStation) { reqs, currentStn ->
                Pair(reqs, currentStn)
            }.collect { (reqs, currentStn) ->
                if (currentStn != null) {
                    // Check if there is an unacknowledged incoming request for this destination station
                    val unackReq = reqs.find {
                        it.destinationStationCode == currentStn.stationCode &&
                                it.status == RequestStatus.PENDING_ACK.name
                    }

                    // Check if there is a 3-station approaching alert for this destination station
                    val approachingReq = reqs.find {
                        it.destinationStationCode == currentStn.stationCode &&
                                it.status == RequestStatus.APPROACHING_3_STATION.name &&
                                !it.secondAlarmAcknowledged
                    }

                    val targetAlert = approachingReq ?: unackReq
                    if (targetAlert != null && _activeAlarmRequest.value?.id != targetAlert.id) {
                        _activeAlarmRequest.value = targetAlert
                        val isHighPriority = targetAlert.status == RequestStatus.APPROACHING_3_STATION.name
                        soundManager.startAlarm(isHighPriority = isHighPriority)
                    }
                }
            }
        }

        // Monitor network-wide broadcast notices ("Other Information") for ALL stations with alarm and popup
        viewModelScope.launch {
            combine(allNotices, _currentStation) { notes, currentStn ->
                Pair(notes, currentStn)
            }.collect { (notes, currentStn) ->
                if (currentStn != null) {
                    // Find an active open notice that has not been dismissed in this session
                    val unhandledNotice = notes.find { notice ->
                        !notice.isClosed && !dismissedNoticeIds.contains(notice.id)
                    }

                    if (unhandledNotice != null && _activeBroadcastNotice.value?.id != unhandledNotice.id && _activeAlarmRequest.value == null) {
                        _activeBroadcastNotice.value = unhandledNotice
                        soundManager.startAlarm(isHighPriority = false)
                    }
                }
            }
        }
    }

    fun login(stationCode: String, staff: String, pass: String): Boolean {
        val cleanPass = pass.trim()
        // Accept "12345", "admin123", "admin", or blank (defaulting to prototype 12345)
        val isValid = cleanPass.isEmpty() || cleanPass == "12345" || cleanPass == "admin123" || cleanPass == "admin"
        if (isValid) {
            viewModelScope.launch(Dispatchers.IO) {
                var stn = stationDao.getStationByCode(stationCode)
                if (stn == null) {
                    stn = StationSeedData.PINK_LINE_STATIONS.find { it.stationCode == stationCode }
                    if (stn != null) {
                        stationDao.insertStation(stn)
                    }
                }
                val resolvedStation = stn ?: StationSeedData.PINK_LINE_STATIONS.find { it.stationCode == "AVIS" }
                ?: StationSeedData.PINK_LINE_STATIONS.first()

                withContext(Dispatchers.Main) {
                    _currentStation.value = resolvedStation
                    _staffName.value = if (staff.isBlank()) "Duty Officer" else staff.trim()
                    _isLoggedIn.value = true
                    _isAdminMode.value = false
                }
                auditDao.insertLog(
                    AuditLog(
                        action = "STATION_LOGIN",
                        details = "Staff ${_staffName.value} logged in at ${resolvedStation.stationName}",
                        stationCode = resolvedStation.stationCode,
                        performedBy = _staffName.value
                    )
                )
            }
            return true
        }
        return false
    }

    fun adminLogin(pass: String): Boolean {
        val cleanPass = pass.trim()
        val isValid = cleanPass.isEmpty() || cleanPass == "12345" || cleanPass == "admin123" || cleanPass == "admin"
        if (isValid) {
            viewModelScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    _currentStation.value = Station("ADMIN", "Central Control & Admin", 0, 0, true)
                    _staffName.value = "Central Administrator"
                    _isLoggedIn.value = true
                    _isAdminMode.value = true
                }
                auditDao.insertLog(
                    AuditLog(
                        action = "ADMIN_LOGIN",
                        details = "Administrator logged into Central Control Portal",
                        stationCode = "CTRL",
                        performedBy = "Central Admin"
                    )
                )
            }
            return true
        }
        return false
    }

    fun switchStation(newStationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stn = stationDao.getStationByCode(newStationCode)
            withContext(Dispatchers.Main) {
                _currentStation.value = stn
                soundManager.stopAlarm()
                _activeAlarmRequest.value = null
            }
            auditDao.insertLog(
                AuditLog(
                    action = "STATION_SWITCH",
                    details = "Station switched to ${stn?.stationName ?: newStationCode} for testing",
                    stationCode = newStationCode,
                    performedBy = _staffName.value
                )
            )
        }
    }

    fun logout() {
        soundManager.stopAlarm()
        _activeAlarmRequest.value = null
        _activeBroadcastNotice.value = null
        _isLoggedIn.value = false
        _isAdminMode.value = false
        _currentStation.value = null
    }

    fun toggleSound() {
        val newState = !_isSoundEnabled.value
        _isSoundEnabled.value = newState
        soundManager.isSoundEnabled = newState
        if (!newState) {
            soundManager.stopAlarm()
        }
    }

    fun testAlarm() {
        if (_isSoundEnabled.value) {
            soundManager.startAlarm(isHighPriority = true)
            viewModelScope.launch {
                kotlinx.coroutines.delay(2500)
                if (_activeAlarmRequest.value == null) {
                    soundManager.stopAlarm()
                }
            }
        }
    }

    fun silenceAlarm() {
        soundManager.stopAlarm()
        _activeBroadcastNotice.value?.let { notice ->
            dismissedNoticeIds.add(notice.id)
        }
        _activeAlarmRequest.value = null
        _activeBroadcastNotice.value = null
    }

    fun createAssistanceRequest(
        assistanceType: String,
        destinationStationCode: String,
        passengerCount: Int,
        trainNumber: String,
        trainSetNumber: String,
        circularDirection: String,
        specialNotes: String = ""
    ) {
        val boarding = _currentStation.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val destStation = stationDao.getStationByCode(destinationStationCode) ?: return@launch
            val reqId = "REQ-${System.currentTimeMillis().toString().takeLast(6)}"
            val request = AssistanceRequest(
                id = reqId,
                assistanceType = assistanceType,
                boardingStationCode = boarding.stationCode,
                boardingStationName = boarding.stationName,
                destinationStationCode = destStation.stationCode,
                destinationStationName = destStation.stationName,
                passengerCount = passengerCount,
                trainNumber = trainNumber.ifBlank { "2890" },
                trainSetNumber = trainSetNumber.ifBlank { "23" },
                circularDirection = circularDirection,
                specialNotes = specialNotes,
                status = RequestStatus.PENDING_ACK.name,
                createdAt = System.currentTimeMillis(),
                createdStaff = _staffName.value
            )

            requestDao.insertRequest(request)
            auditDao.insertLog(
                AuditLog(
                    action = "REQUEST_CREATED",
                    details = "New $assistanceType request $reqId created for ${destStation.stationName} (Train: $trainNumber, Set: $trainSetNumber, Dir: $circularDirection)",
                    stationCode = boarding.stationCode,
                    performedBy = _staffName.value
                )
            )

            soundManager.playSingleChime()
        }
    }

    fun acknowledgeRequest(requestId: String) {
        val stn = _currentStation.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val req = requestDao.getRequestById(requestId) ?: return@launch
            val updated = req.copy(
                status = RequestStatus.ACKNOWLEDGED.name,
                acknowledgedAt = System.currentTimeMillis(),
                acknowledgedStaff = _staffName.value
            )
            requestDao.updateRequest(updated)
            auditDao.insertLog(
                AuditLog(
                    action = "REQUEST_ACKNOWLEDGED",
                    details = "Request $requestId acknowledged by ${stn.stationName} (${_staffName.value})",
                    stationCode = stn.stationCode,
                    performedBy = _staffName.value
                )
            )

            withContext(Dispatchers.Main) {
                if (_activeAlarmRequest.value?.id == requestId) {
                    silenceAlarm()
                }
            }
        }
    }

    fun markTrainDeparted(requestId: String) {
        val stn = _currentStation.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val req = requestDao.getRequestById(requestId) ?: return@launch
            val updated = req.copy(
                status = RequestStatus.DEPARTED.name,
                departedAt = System.currentTimeMillis(),
                currentStationCode = req.boardingStationCode,
                currentStationName = req.boardingStationName
            )
            requestDao.updateRequest(updated)

            // Log train movement
            requestDao.insertTrainMovement(
                TrainMovement(
                    requestId = requestId,
                    stationCode = req.boardingStationCode,
                    stationName = req.boardingStationName,
                    remainingStations = 0,
                    updatedBy = _staffName.value
                )
            )

            auditDao.insertLog(
                AuditLog(
                    action = "TRAIN_DEPARTED",
                    details = "Train ${req.trainNumber} departed from ${req.boardingStationName}",
                    stationCode = stn.stationCode,
                    performedBy = _staffName.value
                )
            )
        }
    }

    fun updateTrainLocation(requestId: String, currentStationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val req = requestDao.getRequestById(requestId) ?: return@launch
            val allStns = stationDao.getAllStationsList()
            val currStn = allStns.find { it.stationCode == currentStationCode } ?: return@launch

            val route = RouteEngine.calculateRoute(
                allStations = allStns,
                boardingCode = req.boardingStationCode,
                destinationCode = req.destinationStationCode,
                direction = req.circularDirection
            )

            val remaining = RouteEngine.calculateRemainingStations(route, currentStationCode)

            // Determine if 3 stations before destination
            val isThreeStationsAway = (remaining == 3)
            val newStatus = when {
                remaining == 0 -> RequestStatus.IN_TRANSIT.name
                isThreeStationsAway && !req.secondAlarmTriggered -> RequestStatus.APPROACHING_3_STATION.name
                else -> RequestStatus.IN_TRANSIT.name
            }

            val triggerSecondAlarm = isThreeStationsAway && !req.secondAlarmTriggered

            val updated = req.copy(
                currentStationCode = currStn.stationCode,
                currentStationName = currStn.stationName,
                currentRemainingStations = remaining,
                status = if (triggerSecondAlarm) RequestStatus.APPROACHING_3_STATION.name else req.status,
                secondAlarmTriggered = req.secondAlarmTriggered || triggerSecondAlarm
            )
            requestDao.updateRequest(updated)

            requestDao.insertTrainMovement(
                TrainMovement(
                    requestId = requestId,
                    stationCode = currStn.stationCode,
                    stationName = currStn.stationName,
                    remainingStations = remaining,
                    updatedBy = _staffName.value
                )
            )

            auditDao.insertLog(
                AuditLog(
                    action = if (triggerSecondAlarm) "SECOND_ALARM_3_STATIONS" else "TRAIN_LOCATION_UPDATED",
                    details = "Train ${req.trainNumber} at ${currStn.stationName} (Remaining: $remaining stations, ETA: ${remaining * 3} mins)",
                    stationCode = currStn.stationCode,
                    performedBy = _staffName.value
                )
            )
        }
    }

    fun acknowledgeSecondAlarm(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val req = requestDao.getRequestById(requestId) ?: return@launch
            val updated = req.copy(
                secondAlarmAcknowledged = true,
                status = RequestStatus.IN_TRANSIT.name
            )
            requestDao.updateRequest(updated)
            withContext(Dispatchers.Main) {
                silenceAlarm()
            }
        }
    }

    fun markAssistanceProvided(requestId: String) {
        val stn = _currentStation.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val req = requestDao.getRequestById(requestId) ?: return@launch
            val updated = req.copy(
                status = RequestStatus.COMPLETED.name,
                completedAt = System.currentTimeMillis(),
                completedStaff = _staffName.value,
                currentRemainingStations = 0
            )
            requestDao.updateRequest(updated)

            auditDao.insertLog(
                AuditLog(
                    action = "ASSISTANCE_COMPLETED",
                    details = "Assistance provided and marked COMPLETED at ${stn.stationName} by ${_staffName.value} for request $requestId",
                    stationCode = stn.stationCode,
                    performedBy = _staffName.value
                )
            )

            withContext(Dispatchers.Main) {
                silenceAlarm()
            }
        }
    }

    // Other Information (Broadcast Notice across ALL stations with alarm and modal popup)
    fun createBroadcastNotice(category: String, description: String, locationDetails: String = "") {
        val stn = _currentStation.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val infoId = "INFO-${System.currentTimeMillis().toString().takeLast(6)}"
            val notice = OtherInformation(
                id = infoId,
                category = category,
                description = description,
                locationDetails = locationDetails,
                originatingStationCode = stn.stationCode,
                originatingStationName = stn.stationName,
                createdStaff = _staffName.value
            )
            otherInfoDao.insertNotice(notice)

            auditDao.insertLog(
                AuditLog(
                    action = "BROADCAST_CREATED",
                    details = "Broadcast notice $infoId ($category) by ${stn.stationName}: $description",
                    stationCode = stn.stationCode,
                    performedBy = _staffName.value
                )
            )

            withContext(Dispatchers.Main) {
                // Remove from dismissed list so that all stations trigger alarm and popup
                dismissedNoticeIds.remove(infoId)
                _activeBroadcastNotice.value = notice
                soundManager.startAlarm(isHighPriority = false)
            }
        }
    }

    fun acknowledgeNotice(infoId: String) {
        val stn = _currentStation.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ack = InformationAcknowledgement(
                infoId = infoId,
                stationCode = stn.stationCode,
                stationName = stn.stationName,
                staffName = _staffName.value
            )
            otherInfoDao.insertAck(ack)
            withContext(Dispatchers.Main) {
                dismissedNoticeIds.add(infoId)
                if (_activeBroadcastNotice.value?.id == infoId) {
                    _activeBroadcastNotice.value = null
                    soundManager.stopAlarm()
                }
            }
        }
    }

    // Train Tracking: Advance train station-by-station based on ~3 min average transit time
    fun advanceTrainToNextStation(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val req = requestDao.getRequestById(requestId) ?: return@launch
            val allStns = stationDao.getAllStationsList()
            val route = RouteEngine.calculateRoute(
                allStations = allStns,
                boardingCode = req.boardingStationCode,
                destinationCode = req.destinationStationCode,
                direction = req.circularDirection
            )
            if (route.isEmpty()) return@launch

            val currIdx = if (req.currentStationCode != null) {
                val idx = route.indexOfFirst { it.stationCode == req.currentStationCode }
                if (idx == -1) 0 else idx
            } else 0

            val nextIdx = (currIdx + 1).coerceAtMost(route.size - 1)
            val nextStation = route[nextIdx]

            updateTrainLocation(requestId, nextStation.stationCode)
        }
    }

    fun closeNotice(infoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val notice = otherInfoDao.getNoticeById(infoId) ?: return@launch
            otherInfoDao.updateNotice(
                notice.copy(
                    isClosed = true,
                    closedAt = System.currentTimeMillis(),
                    closedBy = _staffName.value
                )
            )
        }
    }

    // Station Master Management (Admin)
    fun addOrUpdateStation(station: Station) {
        viewModelScope.launch(Dispatchers.IO) {
            stationDao.insertStation(station)
            auditDao.insertLog(
                AuditLog(
                    action = "STATION_UPDATED",
                    details = "Station ${station.stationName} (${station.stationCode}) updated. Seq: +${station.plusSequence}/-${station.minusSequence}, Active: ${station.isActive}",
                    stationCode = station.stationCode,
                    performedBy = _staffName.value
                )
            )
        }
    }

    fun toggleStationActive(station: Station) {
        addOrUpdateStation(station.copy(isActive = !station.isActive))
    }

    fun resetToPinkLineMaster() {
        viewModelScope.launch(Dispatchers.IO) {
            stationDao.insertStations(StationSeedData.PINK_LINE_STATIONS)
            auditDao.insertLog(
                AuditLog(
                    action = "MASTER_RESET",
                    details = "Reset station master database to 38 Pink Line verified demo stations",
                    stationCode = "CTRL",
                    performedBy = _staffName.value
                )
            )
        }
    }

    suspend fun getTrainMovements(requestId: String): List<TrainMovement> {
        return withContext(Dispatchers.IO) {
            requestDao.getTrainMovementsList(requestId)
        }
    }
}

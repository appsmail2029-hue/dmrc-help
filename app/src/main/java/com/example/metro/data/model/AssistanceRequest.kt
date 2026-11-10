package com.example.metro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AssistanceType(val label: String) {
    VISUALLY_IMPAIRED("Visually Impaired"),
    WHEELCHAIR("Wheelchair Assistance")
}

enum class RequestStatus(val label: String) {
    PENDING_ACK("Pending Ack"),
    ACKNOWLEDGED("Acknowledged"),
    DEPARTED("Train Departed"),
    IN_TRANSIT("In Transit"),
    APPROACHING_3_STATION("3 Stations Away (Alert)"),
    COMPLETED("Completed")
}

@Entity(tableName = "assistance_requests")
data class AssistanceRequest(
    @PrimaryKey val id: String,
    val assistanceType: String, // VISUALLY_IMPAIRED or WHEELCHAIR
    val boardingStationCode: String,
    val boardingStationName: String,
    val destinationStationCode: String,
    val destinationStationName: String,
    val passengerCount: Int,
    val trainNumber: String,
    val trainSetNumber: String,
    val circularDirection: String, // "+ Circular" or "- Circular"
    val specialNotes: String = "",
    val status: String = RequestStatus.PENDING_ACK.name,
    val createdAt: Long = System.currentTimeMillis(),
    val createdStaff: String = "Duty Officer",
    val acknowledgedAt: Long? = null,
    val acknowledgedStaff: String? = null,
    val departedAt: Long? = null,
    val currentStationCode: String? = null,
    val currentStationName: String? = null,
    val currentRemainingStations: Int? = null,
    val secondAlarmTriggered: Boolean = false,
    val secondAlarmAcknowledged: Boolean = false,
    val completedAt: Long? = null,
    val completedStaff: String? = null
)

@Entity(tableName = "train_movements")
data class TrainMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestId: String,
    val stationCode: String,
    val stationName: String,
    val remainingStations: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val updatedBy: String = "Station Staff"
)

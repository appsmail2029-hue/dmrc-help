package com.example.metro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "other_information")
data class OtherInformation(
    @PrimaryKey val id: String,
    val category: String, // "LOST & FOUND", "SEPARATE PASSENGER", "OTHER"
    val description: String,
    val locationDetails: String = "",
    val originatingStationCode: String,
    val originatingStationName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val createdStaff: String = "Duty Officer",
    val isClosed: Boolean = false,
    val closedAt: Long? = null,
    val closedBy: String? = null
)

@Entity(tableName = "information_acknowledgements")
data class InformationAcknowledgement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val infoId: String,
    val stationCode: String,
    val stationName: String,
    val acknowledgedAt: Long = System.currentTimeMillis(),
    val staffName: String = "Duty Officer"
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val details: String,
    val stationCode: String,
    val performedBy: String,
    val timestamp: Long = System.currentTimeMillis()
)

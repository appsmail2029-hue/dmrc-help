package com.example.metro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.AuditLog
import com.example.metro.data.model.InformationAcknowledgement
import com.example.metro.data.model.OtherInformation
import com.example.metro.data.model.Station
import com.example.metro.data.model.TrainMovement
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {
    @Query("SELECT * FROM stations ORDER BY plusSequence ASC")
    fun getAllStationsFlow(): Flow<List<Station>>

    @Query("SELECT * FROM stations WHERE isActive = 1 ORDER BY plusSequence ASC")
    fun getActiveStationsFlow(): Flow<List<Station>>

    @Query("SELECT * FROM stations ORDER BY plusSequence ASC")
    suspend fun getAllStationsList(): List<Station>

    @Query("SELECT * FROM stations WHERE stationCode = :code LIMIT 1")
    suspend fun getStationByCode(code: String): Station?

    @Query("SELECT COUNT(*) FROM stations")
    suspend fun getStationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<Station>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: Station)

    @Update
    suspend fun updateStation(station: Station)

    @Delete
    suspend fun deleteStation(station: Station)
}

@Dao
interface AssistanceRequestDao {
    @Query("SELECT * FROM assistance_requests ORDER BY createdAt DESC")
    fun getAllRequestsFlow(): Flow<List<AssistanceRequest>>

    @Query("SELECT * FROM assistance_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestById(id: String): AssistanceRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: AssistanceRequest)

    @Update
    suspend fun updateRequest(request: AssistanceRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainMovement(movement: TrainMovement)

    @Query("SELECT * FROM train_movements WHERE requestId = :requestId ORDER BY timestamp ASC")
    fun getTrainMovementsFlow(requestId: String): Flow<List<TrainMovement>>

    @Query("SELECT * FROM train_movements WHERE requestId = :requestId ORDER BY timestamp ASC")
    suspend fun getTrainMovementsList(requestId: String): List<TrainMovement>
}

@Dao
interface OtherInformationDao {
    @Query("SELECT * FROM other_information ORDER BY timestamp DESC")
    fun getAllNoticesFlow(): Flow<List<OtherInformation>>

    @Query("SELECT * FROM other_information WHERE id = :id LIMIT 1")
    suspend fun getNoticeById(id: String): OtherInformation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: OtherInformation)

    @Update
    suspend fun updateNotice(notice: OtherInformation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAck(ack: InformationAcknowledgement)

    @Query("SELECT * FROM information_acknowledgements WHERE infoId = :infoId")
    fun getAcksForNoticeFlow(infoId: String): Flow<List<InformationAcknowledgement>>
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllLogsFlow(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog)
}

package com.example.metro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.metro.data.model.AssistanceRequest
import com.example.metro.data.model.AuditLog
import com.example.metro.data.model.InformationAcknowledgement
import com.example.metro.data.model.OtherInformation
import com.example.metro.data.model.Station
import com.example.metro.data.model.StationSeedData
import com.example.metro.data.model.TrainMovement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Station::class,
        AssistanceRequest::class,
        TrainMovement::class,
        OtherInformation::class,
        InformationAcknowledgement::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MetroDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
    abstract fun assistanceRequestDao(): AssistanceRequestDao
    abstract fun otherInformationDao(): OtherInformationDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: MetroDatabase? = null

        fun getInstance(context: Context): MetroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MetroDatabase::class.java,
                    "metro_assist_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial Pink line master stations
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).stationDao().insertStations(
                                    StationSeedData.PINK_LINE_STATIONS
                                )
                                getInstance(context).auditLogDao().insertLog(
                                    AuditLog(
                                        action = "DATABASE_INITIALIZED",
                                        details = "Seeded 38 Delhi Metro Pink Line stations",
                                        stationCode = "SYS",
                                        performedBy = "System Boot"
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

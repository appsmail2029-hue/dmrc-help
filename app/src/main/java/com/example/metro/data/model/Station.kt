package com.example.metro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class Station(
    @PrimaryKey val stationCode: String,
    val stationName: String,
    val plusSequence: Int,
    val minusSequence: Int,
    val isActive: Boolean = true
)

object StationSeedData {
    // Demonstration synthetic sequence for all 38 Pink Line stations (Delhi Metro Pink Line prototype)
    val PINK_LINE_STATIONS = listOf(
        Station("MJPR", "Majlis Park", 1, 38, true),
        Station("AZDP", "Azadpur", 2, 37, true),
        Station("SLMB", "Shalimar Bagh", 3, 36, true),
        Station("NSP", "Netaji Subhash Place", 4, 35, true),
        Station("SKPR", "Shakurpur", 5, 34, true),
        Station("PBGW", "Punjabi Bagh West", 6, 33, true),
        Station("ESIB", "ESI - Basaidarapur", 7, 32, true),
        Station("RJGD", "Rajouri Garden", 8, 31, true),
        Station("MYPR", "Maya Puri", 9, 30, true),
        Station("NRVH", "Naraina Vihar", 10, 29, true),
        Station("DLCT", "Delhi Cantt", 11, 28, true),
        Station("DSKC", "Durgabai Deshmukh South Campus", 12, 27, true),
        Station("SMVM", "Sir M. Vishweshwaraiah Moti Bagh", 13, 26, true),
        Station("BCMP", "Bhikaji Cama Place", 14, 25, true),
        Station("SJNR", "Sarojini Nagar", 15, 24, true),
        Station("DHIN", "Dilli Haat - INA", 16, 23, true),
        Station("SOEX", "South Extension", 17, 22, true),
        Station("LJPN", "Lajpat Nagar", 18, 21, true),
        Station("VNBP", "Vinobapuri", 19, 20, true),
        Station("ASRM", "Ashram", 20, 19, true),
        Station("SKKN", "Sarai Kale Khan - Nizamuddin", 21, 18, true),
        Station("MVH1", "Mayur Vihar - I", 22, 17, true),
        Station("MVP1", "Mayur Vihar Pocket 1", 23, 16, true),
        Station("TPSL", "Trilokpuri Sanjay Lake", 24, 15, true),
        Station("EVNM", "East Vinod Nagar - Mayur Vihar-II", 25, 14, true),
        Station("MWVN", "Mandawali - West Vinod Nagar", 26, 13, true),
        Station("IPEX", "I.P. Extension", 27, 12, true),
        Station("AVIS", "Anand Vihar ISBT", 28, 11, true),
        Station("KKRD", "Karkarduma", 29, 10, true),
        Station("KKRC", "Karkarduma Court", 30, 9, true),
        Station("KRNG", "Krishna Nagar", 31, 8, true),
        Station("EAZN", "East Azad Nagar", 32, 7, true),
        Station("WLCM", "Welcome", 33, 6, true),
        Station("JFBD", "Jafrabad", 34, 5, true),
        Station("MJPR2", "Maujpur - Babarpur", 35, 4, true),
        Station("GKLP", "Gokulpuri", 36, 3, true),
        Station("JHEN", "Johri Enclave", 37, 2, true),
        Station("SHVR", "Shiv Vihar", 38, 1, true)
    )
}

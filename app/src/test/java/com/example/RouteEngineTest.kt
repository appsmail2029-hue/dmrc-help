package com.example

import com.example.metro.data.model.RouteEngine
import com.example.metro.data.model.StationSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteEngineTest {

    @Test
    fun testPinkLineStationsSeeded() {
        val stations = StationSeedData.PINK_LINE_STATIONS
        assertEquals(38, stations.size)

        val anandVihar = stations.find { it.stationCode == "AVIS" }
        assertNotNull(anandVihar)
        assertEquals("Anand Vihar ISBT", anandVihar?.stationName)

        val lajpatNagar = stations.find { it.stationCode == "LJPN" }
        assertNotNull(lajpatNagar)
        assertEquals("Lajpat Nagar", lajpatNagar?.stationName)
    }

    @Test
    fun testMinusCircularRouteFromAnandViharToLajpatNagar() {
        val stations = StationSeedData.PINK_LINE_STATIONS
        val route = RouteEngine.calculateRoute(
            allStations = stations,
            boardingCode = "AVIS",
            destinationCode = "LJPN",
            direction = "- Circular"
        )

        assertTrue(route.isNotEmpty())
        assertEquals("AVIS", route.first().stationCode)
        assertEquals("LJPN", route.last().stationCode)

        // Find Sarai Kale Khan - Nizamuddin (SKKN) on route
        val skknIdx = route.indexOfFirst { it.stationCode == "SKKN" }
        assertTrue("SKKN must be on the route", skknIdx >= 0)

        // Check remaining stations from SKKN to LJPN
        val remainingFromSkkn = RouteEngine.calculateRemainingStations(route, "SKKN")
        assertEquals("SKKN should be exactly 3 stations before Lajpat Nagar in - Circular", 3, remainingFromSkkn)

        // Check ETA
        val eta = RouteEngine.calculateEtaMinutes(remainingFromSkkn)
        assertEquals("ETA should be 9 minutes (3 mins per station)", 9, eta)
    }

    @Test
    fun testStationMasterValidation() {
        val stations = StationSeedData.PINK_LINE_STATIONS
        val result = RouteEngine.validateStationMaster(stations)
        assertTrue("Seed master data must be 100% valid with no duplicate codes/names/sequences", result.isValid)
        assertTrue(result.errors.isEmpty())
    }
}

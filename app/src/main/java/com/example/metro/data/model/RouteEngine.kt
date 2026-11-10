package com.example.metro.data.model

object RouteEngine {
    /**
     * Computes the ordered sequence of stations from boarding to destination
     * based on circular direction (+ Circular or - Circular).
     */
    fun calculateRoute(
        allStations: List<Station>,
        boardingCode: String,
        destinationCode: String,
        direction: String
    ): List<Station> {
        val activeStations = allStations.filter { it.isActive }
        val boarding = activeStations.find { it.stationCode == boardingCode } ?: return emptyList()
        val dest = activeStations.find { it.stationCode == destinationCode } ?: return emptyList()

        if (boarding.stationCode == dest.stationCode) {
            return listOf(boarding)
        }

        val isPlus = direction.contains("+") || direction.contains("Plus", ignoreCase = true)

        return if (isPlus) {
            // Sort by plusSequence (1 to N)
            val sorted = activeStations.sortedBy { it.plusSequence }
            val bIdx = sorted.indexOfFirst { it.stationCode == boardingCode }
            val dIdx = sorted.indexOfFirst { it.stationCode == destinationCode }

            if (bIdx == -1 || dIdx == -1) return emptyList()

            if (bIdx <= dIdx) {
                sorted.subList(bIdx, dIdx + 1)
            } else {
                // Circular wrap around (e.g. from station 30 -> 38 -> 1 -> 5)
                sorted.subList(bIdx, sorted.size) + sorted.subList(0, dIdx + 1)
            }
        } else {
            // Minus Circular - sorted by minusSequence (1 to N)
            val sorted = activeStations.sortedBy { it.minusSequence }
            val bIdx = sorted.indexOfFirst { it.stationCode == boardingCode }
            val dIdx = sorted.indexOfFirst { it.stationCode == destinationCode }

            if (bIdx == -1 || dIdx == -1) return emptyList()

            if (bIdx <= dIdx) {
                sorted.subList(bIdx, dIdx + 1)
            } else {
                sorted.subList(bIdx, sorted.size) + sorted.subList(0, dIdx + 1)
            }
        }
    }

    /**
     * Calculates the number of stations remaining from currentStation to destination.
     * Returns -1 if currentStation is not on route.
     */
    fun calculateRemainingStations(
        route: List<Station>,
        currentStationCode: String
    ): Int {
        val currIdx = route.indexOfFirst { it.stationCode == currentStationCode }
        if (currIdx == -1) return -1
        return (route.size - 1) - currIdx
    }

    /**
     * Returns estimated arrival time in minutes (3 minutes per remaining station).
     */
    fun calculateEtaMinutes(remainingStations: Int): Int {
        return (remainingStations.coerceAtLeast(0)) * 3
    }

    /**
     * Validates station master records.
     */
    fun validateStationMaster(stations: List<Station>): ValidationResult {
        val errors = mutableListOf<String>()

        val duplicateCodes = stations.groupBy { it.stationCode.trim().uppercase() }
            .filter { it.value.size > 1 }
            .keys
        if (duplicateCodes.isNotEmpty()) {
            errors.add("Duplicate station codes found: ${duplicateCodes.joinToString()}")
        }

        val duplicateNames = stations.groupBy { it.stationName.trim().lowercase() }
            .filter { it.value.size > 1 }
            .keys
        if (duplicateNames.isNotEmpty()) {
            errors.add("Duplicate station names found: ${duplicateNames.joinToString()}")
        }

        val duplicatePlus = stations.groupBy { it.plusSequence }
            .filter { it.value.size > 1 }
            .keys
        if (duplicatePlus.isNotEmpty()) {
            errors.add("Duplicate + Circular sequence numbers: ${duplicatePlus.joinToString()}")
        }

        val duplicateMinus = stations.groupBy { it.minusSequence }
            .filter { it.value.size > 1 }
            .keys
        if (duplicateMinus.isNotEmpty()) {
            errors.add("Duplicate - Circular sequence numbers: ${duplicateMinus.joinToString()}")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )
}

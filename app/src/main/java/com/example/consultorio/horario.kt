package com.example.consultorio

object horario {

    private val horarioMap = mapOf(
        0 to "2:00 PM",
        1 to "3:00 PM",
        2 to "4:00 PM",
        3 to "5:00 PM",
        4 to "6:00 PM"
    )
    fun getAvailableTimes(): List<String> {
        return horarioMap.values.toList()
    }
}
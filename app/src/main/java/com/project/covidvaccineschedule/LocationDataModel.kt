package com.project.covidvaccineschedule

import android.content.Context

data class LocationDataModel (
    val name: String,
    val city: String
)
fun getLocationByName(name: String): LocationDataModel? {
    return locations.find { it.name == name }
}

data class BookingDetails(
    val name: String,
    val doseType: String,
    val center: String,
    val date: String,
    val time: String
) {
    override fun toString(): String {
        return "$name|$doseType|$center|$date|$time"
    }

    companion object {
        fun fromString(string: String): BookingDetails {
            val parts = string.split("|")
            return if (parts.size == 5) {
                BookingDetails(
                    name = parts[0],
                    doseType = parts[1],
                    center = parts[2],
                    date = parts[3],
                    time = parts[4]
                )
            } else {
                // Handle the case where the string is not in the expected format
                // Returning a default or empty BookingDetails instance
                BookingDetails(
                    name = "Unknown",
                    doseType = "Unknown",
                    center = "Unknown",
                    date = "Unknown",
                    time = "Unknown"
                )
            }
        }
    }
}



object SharedPreferencesHelper {
    private const val PREFS_NAME = "covid_schedule_prefs"
    private const val BOOKING_HISTORY_KEY = "booking_history"

    fun saveBooking(context: Context, bookingDetails: BookingDetails) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bookings = sharedPreferences.getStringSet(BOOKING_HISTORY_KEY, mutableSetOf()) ?: mutableSetOf()
        bookings.add(bookingDetails.toString())
        editor.putStringSet(BOOKING_HISTORY_KEY, bookings)
        editor.apply()
    }

    fun getBookingHistory(context: Context): Set<BookingDetails> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val bookingsStringSet = sharedPreferences.getStringSet(BOOKING_HISTORY_KEY, mutableSetOf()) ?: mutableSetOf()
        return bookingsStringSet.map { BookingDetails.fromString(it) }.toSet()
    }
}

val locations = listOf(
    LocationDataModel("Ashton Gate football stadium", "Bristol"),
    LocationDataModel("Olympic Office Centre", "Wembley, north London"),
    LocationDataModel("Peterborough City Care Centre", "Peterborough"),
    LocationDataModel("Redgrave Children’s Centre", "Luton"),
    LocationDataModel("Hawks Road Health Clinic", "Kingston, London"),
    LocationDataModel("Former Wickes Store", "Mansfield"),
    LocationDataModel("Bedford Heights", "Bedford"),
    LocationDataModel("Unit 30, Grafton Centre", "Cambridge"),
    LocationDataModel("Avanti Meadows School", "Hertfordshire"),
    LocationDataModel("North Walsham Community Centre", "North Walsham"),
    LocationDataModel("Network House", "London"),
    LocationDataModel("St Thomas’s Hospital", "London"),
    LocationDataModel("Aston Villa FC", "Birmingham"),
    LocationDataModel("Elgar House", "Hereford"),
    LocationDataModel("St Peters Church", "Worcester"),
    LocationDataModel("John Smith’s Stadium", "Huddersfield")
)

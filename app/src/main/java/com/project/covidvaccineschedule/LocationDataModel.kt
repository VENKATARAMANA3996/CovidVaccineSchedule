package com.project.covidvaccineschedule

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
)

object SharedPreferencesHelper {
    private const val PREFS_NAME = "BookingPrefs"
    private const val KEY_BOOKING_DETAILS = "booking_details"

    fun saveBooking(context: Context, bookingDetails: BookingDetails) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Get existing bookings
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_BOOKING_DETAILS, null)
        val bookingListType = object : TypeToken<MutableList<BookingDetails>>() {}.type
        val bookingHistory: MutableList<BookingDetails> = gson.fromJson(json, bookingListType) ?: mutableListOf()

        // Add new booking
        bookingHistory.add(bookingDetails)
        val updatedJson = gson.toJson(bookingHistory)

        editor.putString(KEY_BOOKING_DETAILS, updatedJson)
        editor.apply()
    }

    fun getBookingHistory(context: Context): List<BookingDetails> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(KEY_BOOKING_DETAILS, null)
        val gson = Gson()
        val bookingListType = object : TypeToken<MutableList<BookingDetails>>() {}.type
        return gson.fromJson(json, bookingListType) ?: listOf()
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

package com.project.covidvaccineschedule

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.covidvaccineschedule.SharedPreferencesHelper.getBookingHistory

@Composable
fun MyBookings() {
    val context = LocalContext.current
    val bookingHistory = remember { mutableStateOf(SharedPreferencesHelper.getBookingHistory(context)) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (bookingHistory.value.isEmpty()) {
            item {
                Text(
                    text = "You don't have any bookings",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(bookingHistory.value) { booking ->
                BookingHistoryItem(booking)
                Log.d("Booking", "MyBookings: " + booking.toString())
            }
        }
    }
}
@Composable
fun BookingHistoryItem(booking: BookingDetails) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Name: ${booking.name}", fontWeight = FontWeight.Bold)
        Text(text = "Dose Type: ${booking.doseType}")
        Text(text = "Center: ${booking.center}")
        Text(text = "Date: ${booking.date}")
        Text(text = "Time: ${booking.time}")
    }
}



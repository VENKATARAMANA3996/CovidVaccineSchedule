package com.project.covidvaccineschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.covidvaccineschedule.ui.theme.CovidVaccineScheduleTheme
import java.sql.Time
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            CovidVaccineScheduleTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "location_list") {
        composable("location_list") {
            LocationList(
                locations = locations,
                onLocationSelected = { location ->
                    navController.navigate("booking_page/${location.name}")
                }
            )
        }

        composable("booking_page/{locationName}") { backStackEntry ->
            val locationName = backStackEntry.arguments?.getString("locationName") ?: ""
            val location = getLocationByName(locationName)
            if (location != null) {
                BookingPage(location = location) {
                    navController.navigate("confirmation_page")
                }
            } else {
                Text("Location not found")
            }
        }

        composable("confirmation_page") {
            ConfirmationPage(navController)
        }
    }
}

@Composable
fun LocationList(locations: List<LocationDataModel>, onLocationSelected: (LocationDataModel) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Select your nearby vaccine centers:", modifier = Modifier.padding(start = 12.dp))

            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                items(locations) { location ->
                    LocationItem(location = location, onClick = { onLocationSelected(location) })
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPage(location: LocationDataModel, onBookingConfirmed: () -> Unit) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var doseType by remember { mutableStateOf("First Dose") }
    var name by remember { mutableStateOf("") }
    val timeSlots = remember { generateTimeSlots() }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Book your vaccine at ",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
                Text(
                    text = "${location.name}",
                    color = Color(0xFF00a57a),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CalendarViewWrapper(
                selectedDate = selectedDate,
                onDateSelected = { date -> selectedDate = date }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Select Time Slot")
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)

            ) {
                items(timeSlots) { timeSlot ->
                    TimeSlotItem(
                        timeSlot = timeSlot,
                        isSelected = (timeSlot == selectedTime),
                        onSelect = { selectedTime = timeSlot }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dose Type Selection
            Text(text = "Select Dose Type")
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (doseType == "First Dose"),
                    onClick = { doseType = "First Dose" },
                    modifier = Modifier.weight(1f)
                )
                Text(text = "First Dose", modifier = Modifier.padding(start = 5.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = (doseType == "Second Dose"),
                    onClick = { doseType = "Second Dose" },
                    modifier = Modifier.weight(1f)
                )
                Text(text = "Second Dose", modifier = Modifier.padding(start = 5.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = (doseType == "Booster Dose"),
                    onClick = { doseType = "Booster Dose" },
                    modifier = Modifier.weight(1f)
                )
                Text(text = "Booster Dose", modifier = Modifier.padding(start = 5.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val bookingDetails = BookingDetails(
                        name = name,
                        doseType = doseType,
                        center = location.name,
                        date = selectedDate?.let { dateFormatter.format(it) } ?: "",
                        time = selectedTime ?: ""
                    )
                    SharedPreferencesHelper.saveBooking(context, bookingDetails)
                    showBookingConfirmationNotification(
                        context = context,
                        centerName = location.name,
                        date = selectedDate?.let { dateFormatter.format(it) } ?: "Date not selected",
                        time = selectedTime ?: "Time not selected"
                    )
                    onBookingConfirmed()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00a57a)
                )
            ) {
                Text(
                    text = "Confirm Booking",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }
    }
}
@Composable
fun ConfirmationPage(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = "Your booking has been confirmed!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("location_list"){
                    popUpTo(0)
                }
            }
        )
        {
            Text(text = "OK")
        }
    }
}

@Composable
fun CalendarViewWrapper(
    selectedDate: Date?,
    onDateSelected: (Date) -> Unit
) {
    var date by remember { mutableStateOf(selectedDate) }
    val context = LocalContext.current
    AndroidView(
        factory = { context ->
            CalendarView(context).apply {
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    date = calendar.time
                    onDateSelected(date!!)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}


fun generateTimeSlots(): List<String> {
    val slots = mutableListOf<String>()
    val calendar = Calendar.getInstance()

    // Start time: 11:00 AM
    calendar.set(Calendar.HOUR_OF_DAY, 11)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    // End time: 3:00 PM
    val endHour = 15 // 3 PM

    while (calendar.get(Calendar.HOUR_OF_DAY) < endHour || (calendar.get(Calendar.HOUR_OF_DAY) == endHour && calendar.get(Calendar.MINUTE) == 0)) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        slots.add(timeFormat.format(calendar.time))
        calendar.add(Calendar.MINUTE, 15)  // Increment by 15 minutes
    }

    return slots
}

fun showBookingConfirmationNotification(
    context: Context,
    centerName: String,
    date: String,
    time: String
) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "booking_notification_channel",
            "Vaccine Booking Notifications", // Channel name
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notification channel for vaccine booking"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, "booking_notification_channel")
        .setSmallIcon(R.drawable.baseline_circle_notifications_24)
        .setContentTitle("Vaccine Schedule Confirmed")
        .setContentText("Your vaccine schedule at $centerName on $date at $time is confirmed.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setAutoCancel(true)
        .build()

    // Log to check if the notification is being triggered
    Log.d("NotificationHelper", "Showing notification: $notification")

    // Show the notification
    notificationManager.notify(1001, notification) // Unique notification ID
    Toast.makeText(context, "Booked Successfully!", Toast.LENGTH_SHORT).show()
}


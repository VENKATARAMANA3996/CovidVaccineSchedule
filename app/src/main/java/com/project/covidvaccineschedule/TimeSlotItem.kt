package com.project.covidvaccineschedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TimeSlotItem(
    timeSlot: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF4CAF50) else Color(0xFF81C784) // Darker green when selected
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(50.dp)
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeSlot,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
package com.project.covidvaccineschedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LocationItem(location: LocationDataModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),

        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row (modifier = Modifier.fillMaxSize().background(Color(0xFF00a57a))){
            Column(
                modifier = Modifier.fillMaxSize().padding(start = 10.dp).background(Color.White).padding(16.dp)
            ) {
                Text(text = location.name, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = location.city, fontSize = 16.sp, color = Color.Gray)
            }
        }

    }
}
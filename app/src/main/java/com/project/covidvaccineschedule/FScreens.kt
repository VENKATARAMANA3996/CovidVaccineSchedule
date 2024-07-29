package com.project.covidvaccineschedule

sealed class FScreens (val screens: String){
    data object Home: FScreens ("Home")
    data object Bookings: FScreens ("Bookings")
    data object FAQs: FScreens ("FAQs")
}
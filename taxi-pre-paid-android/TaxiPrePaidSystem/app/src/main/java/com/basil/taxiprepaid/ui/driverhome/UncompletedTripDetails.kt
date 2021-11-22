package com.basil.taxiprepaid.ui.driverhome

import android.os.Parcelable
import android.widget.TextView
import com.basil.taxiprepaid.R
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class UncompletedTripDetails(
    var tripId: Int,
    var destination: String,
    var passengerName: String,
    var passengerContact: String,
    var bookingTime: Long
) :Parcelable {

    fun formatTime(bookingTime: Long): String {
        return if (bookingTime != 0L) bookingTime.toDateTime() else "--"
    }

    fun Long.toDateTime(): String {
        return if (this != 0.toLong()) {
            val formatter = SimpleDateFormat("dd-MMM-yyyy, HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            formatter.format(calendar.time)
        } else {
            ""
        }
    }
}
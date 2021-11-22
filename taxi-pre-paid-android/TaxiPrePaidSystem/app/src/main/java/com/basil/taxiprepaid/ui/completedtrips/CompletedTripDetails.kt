package com.basil.taxiprepaid.ui.completedtrips

import android.os.Parcelable
import android.widget.TextView
import com.basil.taxiprepaid.R
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class CompletedTripDetails(
    var tripId: Int,
    var destination: String,
    var passengerName: String,
    var amount: Int,
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

    fun getString(number: Int): String {
        return number.toString()
    }
}
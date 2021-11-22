package com.basil.taxiprepaid.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LoginRequest(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("password")
    val password: String
)

@Keep
data class BookTaxiRequest(
    @SerializedName("passenger_name")
    val passengerName: String,
    @SerializedName("passenger_phone")
    val passengerPhone: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("distance")
    val distance: Int
)

@Keep
data class CompleteTripRequest(
    @SerializedName("trip_id")
    val tripId: Int,
    @SerializedName("otp")
    val otp: Int
)
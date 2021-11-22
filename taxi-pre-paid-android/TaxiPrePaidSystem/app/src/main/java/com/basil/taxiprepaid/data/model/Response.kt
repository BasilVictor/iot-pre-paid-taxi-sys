package com.basil.taxiprepaid.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Keep
@Parcelize
data class LoginResponse(
    @SerializedName("access-token")
    val token: String,
    @SerializedName("user_type")
    val userType : Int,
    @SerializedName("address")
    val address: String?
) : Parcelable

@Keep
@Parcelize
data class BookTaxiResponse(
    @SerializedName("vehicle_id")
    val vehicleId: String
) : Parcelable

@Keep
@Parcelize
data class UncompletedTripResponse(
    @SerializedName("trip_id")
    val tripId: Int,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("passenger_name")
    val passengerName: String,
    @SerializedName("passenger_contact")
    val passengerContact: String,
    @SerializedName("booking_time")
    val bookingTime: Long
) : Parcelable

@Keep
@Parcelize
data class CompletedTripResponse(
    @SerializedName("trip_id")
    val tripId: Int,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("passenger_name")
    val passengerName: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("booking_time")
    val bookingTime: Long
) : Parcelable

@Keep
@Parcelize
data class GetCostResponse(
    @SerializedName("cost")
    val cost: Int
) : Parcelable

@Keep
@Parcelize
data class GetPayoutResponse(
    @SerializedName("balance")
    val balance: Int
) : Parcelable

@Keep
@Parcelize
data class DistanceMatrixResponse (
    @SerializedName("destination_addresses")
    val destination_addresses : List<String>,
    @SerializedName("origin_addresses")
    val origin_addresses : List<String>,
    @SerializedName("rows")
    val rows : List<Rows>,
    @SerializedName("status")
    val status : String
) : Parcelable

@Keep
@Parcelize
data class Rows (
    @SerializedName("elements")
    val elements : List<Elements>
) : Parcelable

@Keep
@Parcelize
data class Elements (
    @SerializedName("distance") val distance : Distance,
    @SerializedName("duration") val duration : Duration,
    @SerializedName("status") val status : String
) : Parcelable

@Keep
@Parcelize
data class Duration (
    @SerializedName("text") val text : String,
    @SerializedName("value") val value : Int
) : Parcelable

@Keep
@Parcelize
data class Distance (
    @SerializedName("text") val text : String,
    @SerializedName("value") val value : Int
) : Parcelable

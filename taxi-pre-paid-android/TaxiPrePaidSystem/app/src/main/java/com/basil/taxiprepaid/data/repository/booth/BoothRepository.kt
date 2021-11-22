package com.basil.taxiprepaid.data.repository.booth

import com.basil.taxiprepaid.data.model.*
import com.basil.taxiprepaid.data.remote.ApiResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.Response

interface BoothRepository {
    fun getDistance(sourceLatLng: String, destLatLng: String, key: String): Single<DistanceMatrixResponse>
    fun getCost(distance: Int): Single<ApiResponse<GetCostResponse>>
    fun bookTaxi(bookTaxiRequest: BookTaxiRequest): Single<ApiResponse<BookTaxiResponse?>>
}
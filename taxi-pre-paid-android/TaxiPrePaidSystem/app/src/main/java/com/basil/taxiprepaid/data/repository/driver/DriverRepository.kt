package com.basil.taxiprepaid.data.repository.driver

import com.basil.taxiprepaid.data.model.CompleteTripRequest
import com.basil.taxiprepaid.data.model.CompletedTripResponse
import com.basil.taxiprepaid.data.model.GetPayoutResponse
import com.basil.taxiprepaid.data.model.UncompletedTripResponse
import com.basil.taxiprepaid.data.remote.ApiResponse
import io.reactivex.rxjava3.core.Single

interface DriverRepository {
    fun getUncompletedTrip(): Single<ApiResponse<List<UncompletedTripResponse>>>
    fun getCompletedTrip(): Single<ApiResponse<List<CompletedTripResponse>>>
    fun getPayout(): Single<ApiResponse<GetPayoutResponse>>
    fun completeTrip(completeTripRequest: CompleteTripRequest): Single<ApiResponse<Any>>
}
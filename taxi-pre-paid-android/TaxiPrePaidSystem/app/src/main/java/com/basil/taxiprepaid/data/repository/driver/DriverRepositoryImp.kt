package com.basil.taxiprepaid.data.repository.driver

import com.basil.taxiprepaid.data.model.CompleteTripRequest
import com.basil.taxiprepaid.data.model.CompletedTripResponse
import com.basil.taxiprepaid.data.model.GetPayoutResponse
import com.basil.taxiprepaid.data.model.UncompletedTripResponse
import com.basil.taxiprepaid.data.remote.ApiResponse
import com.basil.taxiprepaid.data.remote.ApiResponseConvertor
import com.basil.taxiprepaid.data.remote.ApiService
import io.reactivex.rxjava3.core.Single

class DriverRepositoryImp(private val apiService: ApiService) : DriverRepository {
    override fun getUncompletedTrip(): Single<ApiResponse<List<UncompletedTripResponse>>> {
        return apiService.getUncompletedTrip().map{
            ApiResponseConvertor.convert(it)
        }
    }

    override fun getCompletedTrip(): Single<ApiResponse<List<CompletedTripResponse>>> {
        return apiService.getCompletedTrip().map{
            ApiResponseConvertor.convert(it)
        }
    }

    override fun getPayout(): Single<ApiResponse<GetPayoutResponse>> {
        return apiService.getPayout().map {
            ApiResponseConvertor.convert(it)
        }
    }

    override fun completeTrip(completeTripRequest: CompleteTripRequest): Single<ApiResponse<Any>> {
        return apiService.completeTrip(completeTripRequest).map{
            ApiResponseConvertor.convert(it)
        }
    }
}
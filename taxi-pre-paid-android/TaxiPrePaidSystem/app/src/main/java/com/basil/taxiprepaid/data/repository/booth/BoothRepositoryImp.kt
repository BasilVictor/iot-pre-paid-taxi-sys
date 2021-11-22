package com.basil.taxiprepaid.data.repository.booth

import android.util.Log
import com.basil.taxiprepaid.data.model.*
import com.basil.taxiprepaid.data.remote.ApiResponse
import com.basil.taxiprepaid.data.remote.ApiResponseConvertor
import com.basil.taxiprepaid.data.remote.ApiService
import com.basil.taxiprepaid.data.remote.MapService
import io.reactivex.rxjava3.core.Single
import okhttp3.Response

class BoothRepositoryImp(private val apiService: ApiService,
                         private val mapService: MapService) : BoothRepository {
    override fun getDistance(sourceLatLng: String, destLatLng: String, key: String): Single<DistanceMatrixResponse> {
        return mapService.getDistanceMatrixResult(sourceLatLng, destLatLng, key)
    }

    override fun getCost(distance: Int): Single<ApiResponse<GetCostResponse>> {
        return apiService.getCost(distance).map {
            ApiResponseConvertor.convert(it)
        }
    }

    override fun bookTaxi(bookTaxiRequest: BookTaxiRequest): Single<ApiResponse<BookTaxiResponse?>> {
        return apiService.bookTaxi(bookTaxiRequest).map{
            ApiResponseConvertor.convert(it)
        }
    }
}
package com.basil.taxiprepaid.data.remote

import com.basil.taxiprepaid.data.model.BaseResponse
import com.basil.taxiprepaid.data.model.DistanceMatrixResponse
import com.basil.taxiprepaid.data.model.UncompletedTripResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.Response
import retrofit2.adapter.rxjava3.Result
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("json")
    fun getDistanceMatrixResult(@Query("origins") sourceLatLng: String,
                                @Query("destinations") destLatLng: String,
                                @Query("key") key: String): Single<DistanceMatrixResponse>
}
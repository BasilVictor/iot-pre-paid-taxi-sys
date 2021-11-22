package com.basil.taxiprepaid.data.remote

import com.basil.taxiprepaid.data.model.*
import io.reactivex.rxjava3.core.Single
import retrofit2.adapter.rxjava3.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Single<Result<BaseResponse<LoginResponse?>>>

    @POST("booth/bookTaxi")
    fun bookTaxi(@Body bookTaxiRequest: BookTaxiRequest): Single<Result<BaseResponse<BookTaxiResponse?>>>

    @GET("booth/getCost/{distance}")
    fun getCost(@Path("distance") distance: Int): Single<Result<BaseResponse<GetCostResponse>>>

    @GET("driver/getUncompletedTrip")
    fun getUncompletedTrip(): Single<Result<BaseResponse<List<UncompletedTripResponse>>>>

    @GET("driver/getCompletedTrip")
    fun getCompletedTrip(): Single<Result<BaseResponse<List<CompletedTripResponse>>>>

    @GET("driver/getPayout")
    fun getPayout(): Single<Result<BaseResponse<GetPayoutResponse>>>

    @POST("driver/completeTrip")
    fun completeTrip(@Body completeTripRequest: CompleteTripRequest): Single<Result<BaseResponse<Any>>>

}
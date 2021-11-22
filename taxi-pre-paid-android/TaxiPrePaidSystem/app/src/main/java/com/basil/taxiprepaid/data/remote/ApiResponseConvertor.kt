package com.basil.taxiprepaid.data.remote

import com.basil.taxiprepaid.data.model.BaseResponse
import retrofit2.adapter.rxjava3.Result
import timber.log.Timber

object ApiResponseConvertor {

    fun <T> convert(error: Result<BaseResponse<T>>): ApiResponse<T> {
        return if(error.isError){
            Timber.e(error.error())
            ApiResponse.create(error.error())
        }else {
            ApiResponse.create(error.response())
        }
    }
}
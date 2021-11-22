package com.basil.taxiprepaid.data.remote

import com.basil.taxiprepaid.data.model.BaseResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import timber.log.Timber


/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable?): ApiErrorResponse<T> {
            return ApiErrorResponse(0, error?.message ?: "Server error")
        }

        fun <T> create(response: Response<BaseResponse<T>>?): ApiResponse<T> {
            return if (response == null) {
                return ApiEmptyResponse()
            } else if (response.isSuccessful) {
                if (response.body()?.status == 1
                    || response.body()?.status == 0
                ) {
                    if (response.body()?.data == null) {
                        if(response.body()?.message != null) {
                            ApiMessageResponse(message = response.body()?.message)
                        } else {
                            ApiEmptyResponse()
                        }
                    } else {
                        ApiSuccessResponse(body = response.body()?.data!!,
                            message = response.body()?.message)
                    }
                } else {
                    ApiErrorResponse(-1, response.body()?.message ?: "Server error")
                }
            } else {
                val gson = GsonBuilder().setPrettyPrinting().create();
                val collectionType = object : TypeToken<BaseResponse<T?>>() {}.type
                try {
                    val data: BaseResponse<Any?> =
                        gson.fromJson(response.errorBody()?.string(), collectionType)
                    ApiErrorResponse(response.code(), data.message!!)
                } catch (e: JsonSyntaxException) {
                    Timber.e(e)
                    ApiErrorResponse(-1, "Server error")
                }
            }
        }

    }
}


class ApiEmptyResponse<T> : ApiResponse<T>()
class ApiEndResponse<T>(
    val body: T
) : ApiResponse<T>()
class ApiMessageResponse<T>(
    val message: String?
) : ApiResponse<T>()
data class ApiSuccessResponse<T>(
    val body: T,
    val message: String?
) : ApiResponse<T>()

data class ApiErrorResponse<T>(val statusCode: Int, val errorMessage: String) : ApiResponse<T>()

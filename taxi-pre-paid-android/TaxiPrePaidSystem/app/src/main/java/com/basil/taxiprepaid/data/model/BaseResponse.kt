package com.basil.taxiprepaid.data.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)

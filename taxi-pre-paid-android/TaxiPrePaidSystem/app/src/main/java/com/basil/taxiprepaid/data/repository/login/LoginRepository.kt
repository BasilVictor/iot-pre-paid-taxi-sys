package com.basil.taxiprepaid.data.repository.login

import com.basil.taxiprepaid.data.model.LoginRequest
import com.basil.taxiprepaid.data.model.LoginResponse
import com.basil.taxiprepaid.data.remote.ApiResponse
import io.reactivex.rxjava3.core.Single

interface LoginRepository {
    fun login(loginRequest: LoginRequest): Single<ApiResponse<LoginResponse?>>
}
package com.basil.taxiprepaid.data.repository.login

import com.basil.taxiprepaid.data.model.LoginRequest
import com.basil.taxiprepaid.data.model.LoginResponse
import com.basil.taxiprepaid.data.remote.ApiResponse
import com.basil.taxiprepaid.data.remote.ApiResponseConvertor
import com.basil.taxiprepaid.data.remote.ApiService
import io.reactivex.rxjava3.core.Single


class LoginRepositoryImp(private val apiService: ApiService) : LoginRepository {
    override fun login(loginRequest: LoginRequest): Single<ApiResponse<LoginResponse?>> {
        return apiService.login(loginRequest).map{
            ApiResponseConvertor.convert(it)
        }
    }
}


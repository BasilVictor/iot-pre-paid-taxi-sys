package com.basil.taxiprepaid.data.remote

import com.roshnee.data.repository.PreferenceRepository
import okhttp3.Interceptor
import okhttp3.Response

class JSONHeaderInterceptor(private val preferenceRepository: PreferenceRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain) : Response {

        val request = chain.request().newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
        if(preferenceRepository.isLogin()){
            request.header("access-token", preferenceRepository.getToken())
        }
        return chain.proceed(request.build())
    }
}
package com.roshnee.data.repository

import com.basil.taxiprepaid.data.local.PreferenceStorage
import com.basil.taxiprepaid.data.model.LoginResponse


class PreferenceRepositoryImp(private val preferenceStorage: PreferenceStorage) :
    PreferenceRepository {

    override fun onSuccessfulLogin(loginResponse: LoginResponse) {
        preferenceStorage.token = loginResponse.token
        preferenceStorage.usertype = loginResponse.userType
        preferenceStorage.loginFlag = true
    }

    override fun isLogin() = preferenceStorage.loginFlag

    override fun clear() {
        preferenceStorage.token = ""
        preferenceStorage.loginFlag = false
        preferenceStorage.usertype = 0
    }

    override fun getToken() = preferenceStorage.token

    override fun getUserType() = preferenceStorage.usertype

    override fun setLocation(latitude: Float, longitude: Float) {
        preferenceStorage.latitude = latitude
        preferenceStorage.longitude = longitude
    }

    override fun getLatitude() = preferenceStorage.latitude

    override fun getLongitude()= preferenceStorage.longitude


}
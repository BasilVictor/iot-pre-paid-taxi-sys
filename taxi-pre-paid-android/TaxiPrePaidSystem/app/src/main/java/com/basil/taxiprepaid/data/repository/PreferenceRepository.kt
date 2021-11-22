package com.roshnee.data.repository

import com.basil.taxiprepaid.data.model.LoginResponse

interface PreferenceRepository {
    fun onSuccessfulLogin(loginResponse: LoginResponse)
    fun isLogin():Boolean
    fun clear()
    fun getToken():String
    fun getUserType(): Int
    fun setLocation(latitude: Float, longitude: Float)
    fun getLatitude(): Float
    fun getLongitude(): Float
}
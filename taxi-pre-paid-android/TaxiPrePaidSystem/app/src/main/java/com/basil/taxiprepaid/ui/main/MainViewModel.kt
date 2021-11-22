package com.basil.taxiprepaid.ui.main

import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.basil.taxiprepaid.data.model.LoginRequest
import com.basil.taxiprepaid.data.remote.ApiEmptyResponse
import com.basil.taxiprepaid.data.remote.ApiErrorResponse
import com.basil.taxiprepaid.data.remote.ApiSuccessResponse
import com.basil.taxiprepaid.data.repository.login.LoginRepository
import com.basil.taxiprepaid.ui.base.ObservableViewModel
import com.basil.taxiprepaid.utils.subscribeSingleOnMain
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.roshnee.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val loginRepository: LoginRepository,
    private val geocoder: Geocoder
): ObservableViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    val userID = ObservableField("")
    val password = ObservableField("")
    val valid = object : ObservableField<Boolean>(userID, password) {
        override fun get(): Boolean {
            return (userID.get()?.isNotEmpty() == true && password.get()?.isNotEmpty() == true)
        }
    }
    private val _moveToBoothScreen = MutableLiveData<Boolean>()
    val moveToBoothScreen = _moveToBoothScreen.asLiveData()
    private val _moveToDriverScreen = MutableLiveData<Boolean>()
    val moveToDriverScreen = _moveToDriverScreen.asLiveData()

    init {
        if(preferenceRepository.isLogin()) {
            if(preferenceRepository.getUserType() == 1) {
                _moveToBoothScreen.postValue(true)
            } else {
                _moveToDriverScreen.postValue(true)
            }
        }
    }


    fun signIn() {
        disposable.add(
            loginRepository.login(LoginRequest(
                userId = userID.get().toString(),
                password = password.get().toString()
            ))
                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                .subscribeSingleOnMain { apiResponse, throwable ->
                    when (apiResponse) {
                        is ApiSuccessResponse -> {
                            apiResponse.body?.let { preferenceRepository.onSuccessfulLogin(it) }
                            if(apiResponse.body?.userType  == 1) {
                                val addressLatLng: List<Address>? =
                                    geocoder.getFromLocationName(apiResponse.body.address, 1)
                                preferenceRepository.setLocation(addressLatLng!![0].latitude.toFloat(),
                                    addressLatLng!![0].longitude.toFloat())
                                _moveToBoothScreen.postValue(true)
                            } else {
                                _moveToDriverScreen.postValue(true)
                            }
                            _error.postValue("")
                        }
                        is ApiErrorResponse -> {
                            _error.postValue(apiResponse.errorMessage)
                        }
                        else -> _error.postValue("error")
                    }
                }
        )
    }

    override fun onCleared() {
        disposable.dispose()
        disposable.clear()
        super.onCleared()
    }
}
package com.basil.taxiprepaid.ui.driverhome

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.basil.taxiprepaid.data.model.CompleteTripRequest
import com.basil.taxiprepaid.data.remote.ApiEmptyResponse
import com.basil.taxiprepaid.data.remote.ApiErrorResponse
import com.basil.taxiprepaid.data.remote.ApiMessageResponse
import com.basil.taxiprepaid.data.remote.ApiSuccessResponse
import com.basil.taxiprepaid.data.repository.driver.DriverRepository
import com.basil.taxiprepaid.ui.base.ObservableViewModel
import com.basil.taxiprepaid.utils.MessageEvent
import com.basil.taxiprepaid.utils.subscribeSingleOnMain
import com.roshnee.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class DriverHomeViewModel @Inject constructor(
    private val driverRepository: DriverRepository
): ObservableViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    private val uncompletedTripTempList = mutableListOf<UncompletedTripDetails>()
    private val _uncompletedTripList = MutableLiveData<List<UncompletedTripDetails>>()
    val uncompletedTripList = _uncompletedTripList.asLiveData()
    private val _snackbarMessage = MutableLiveData<MessageEvent<String>>()
    val snackbarMessage: LiveData<MessageEvent<String>> get() = _snackbarMessage

    fun fetchUncompletedTrips() {
        _error.postValue("")
        disposable.add(
            driverRepository.getUncompletedTrip()
                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                .subscribeSingleOnMain { apiResponse, throwable ->
                    uncompletedTripTempList.clear()
                    when (apiResponse) {
                        is ApiSuccessResponse -> {
                            apiResponse.body.forEach { uncompletedTrip ->
                                uncompletedTripTempList.add(
                                    UncompletedTripDetails(
                                        tripId = uncompletedTrip.tripId,
                                        destination = uncompletedTrip.destination,
                                        passengerName = uncompletedTrip.passengerName,
                                        passengerContact = uncompletedTrip.passengerContact,
                                        bookingTime = uncompletedTrip.bookingTime
                                    )
                                )
                            }
                            _uncompletedTripList.postValue(uncompletedTripTempList)
                        }
                        is ApiMessageResponse -> {
                            _snackbarMessage.postValue(MessageEvent(apiResponse.message?:""))
                        }
                        is ApiErrorResponse -> {
                            _error.postValue(apiResponse.errorMessage)
                        }
                        else -> _error.postValue("error")
                    }
                }
        )
    }

    fun markTripCompleted(position: Int, otp: Int) {
        disposable.add(
            driverRepository.completeTrip(
                CompleteTripRequest(
                 tripId = uncompletedTripTempList[position].tripId,
                 otp = otp
            ))
                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                .subscribeSingleOnMain { apiResponse, throwable ->
                    when (apiResponse) {
                        is ApiErrorResponse -> {
                            _error.postValue(apiResponse.errorMessage)
                        }
                        is ApiMessageResponse -> {
                            _snackbarMessage.postValue(MessageEvent(apiResponse.message?:""))
                            fetchUncompletedTrips()
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
package com.basil.taxiprepaid.ui.completedtrips

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.basil.taxiprepaid.data.remote.ApiErrorResponse
import com.basil.taxiprepaid.data.remote.ApiMessageResponse
import com.basil.taxiprepaid.data.remote.ApiSuccessResponse
import com.basil.taxiprepaid.data.repository.driver.DriverRepository
import com.basil.taxiprepaid.ui.base.ObservableViewModel
import com.basil.taxiprepaid.utils.MessageEvent
import com.basil.taxiprepaid.utils.subscribeSingleOnMain
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class CompletedTripsViewModel @Inject constructor(
    private val driverRepository: DriverRepository
): ObservableViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    private val completedTripTempList = mutableListOf<CompletedTripDetails>()
    private val _completedTripList = MutableLiveData<List<CompletedTripDetails>>()
    val completedTripList = _completedTripList.asLiveData()
    private val _snackbarMessage = MutableLiveData<MessageEvent<String>>()
    val snackbarMessage: LiveData<MessageEvent<String>> get() = _snackbarMessage

    fun fetchCompletedTrips() {
        disposable.add(
            driverRepository.getCompletedTrip()
                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                .subscribeSingleOnMain { apiResponse, throwable ->
                    completedTripTempList.clear()
                    when (apiResponse) {
                        is ApiSuccessResponse -> {
                            apiResponse.body.forEach { completedTrip ->
                                completedTripTempList.add(
                                    CompletedTripDetails(
                                        tripId = completedTrip.tripId,
                                        destination = completedTrip.destination,
                                        passengerName = completedTrip.passengerName,
                                        amount = completedTrip.amount,
                                        bookingTime = completedTrip.bookingTime
                                    )
                                )
                            }
                            _completedTripList.postValue(completedTripTempList)
                            _error.postValue("")
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

    override fun onCleared() {
        disposable.dispose()
        disposable.clear()
        super.onCleared()
    }
}
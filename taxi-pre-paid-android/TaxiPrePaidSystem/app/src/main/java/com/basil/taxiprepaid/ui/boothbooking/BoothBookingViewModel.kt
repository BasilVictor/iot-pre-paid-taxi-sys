package com.basil.taxiprepaid.ui.boothbooking

import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.basil.taxiprepaid.BuildConfig
import com.basil.taxiprepaid.data.model.BookTaxiRequest
import com.basil.taxiprepaid.data.remote.ApiErrorResponse
import com.basil.taxiprepaid.data.remote.ApiSuccessResponse
import com.basil.taxiprepaid.data.repository.booth.BoothRepository
import com.basil.taxiprepaid.ui.base.ObservableViewModel
import com.basil.taxiprepaid.utils.subscribeSingleOnMain
import com.roshnee.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject


@HiltViewModel
class BoothBookingViewModel @Inject constructor(
    private val boothRepository: BoothRepository,
    private val preferenceRepository: PreferenceRepository
): ObservableViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    val name = ObservableField("")
    val phone = ObservableField("")
    val destination = ObservableField("")
    val valid = object : ObservableField<Boolean>(name, phone, destination) {
        override fun get(): Boolean {
            return (name.get()?.isNotEmpty() == true && phone.get()?.isNotEmpty() == true
                    && destination.get()?.isNotEmpty() == true)
        }
    }

    private val _moveToAutoCompleteScreen = MutableLiveData<Boolean>()
    val moveToAutoCompleteScreen = _moveToAutoCompleteScreen.asLiveData()
    private val _vehicleBooked = MutableLiveData<Boolean>()
    val vehicleBooked = _vehicleBooked.asLiveData()
    val vehicleId = ObservableField("")
    private val _cost = MutableLiveData<Int>()
    val cost = _cost.asLiveData()

    var destLat = 0.0f;
    var destLng = 0.0f;
    var distance = 0;

    fun getDestinationAddress() {
        _moveToAutoCompleteScreen.postValue(true)
    }

    fun bookTaxi() {
        disposable.add(
            boothRepository.getDistance(
                "${preferenceRepository.getLatitude()} ${preferenceRepository.getLongitude()}",
                "$destLat $destLng",
                BuildConfig.MAPS_API_KEY
            )
                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                .subscribeSingleOnMain { apiResponse, throwable ->
                    if(apiResponse?.status == "OK") {
                        distance = apiResponse.rows[0].elements[0].distance.value
                        disposable.add(
                            boothRepository.getCost(distance)
                                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                                .subscribeSingleOnMain { apiResponse, throwable ->
                                    when (apiResponse) {
                                        is ApiSuccessResponse -> {
                                            _cost.postValue(apiResponse.body.cost)
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
                }
        )
    }

    fun confirmTaxi() {
        disposable.add(
            boothRepository.bookTaxi(
                BookTaxiRequest(
                passengerName = name.get().toString(),
                passengerPhone = phone.get().toString(),
                destination = destination.get().toString(),
                distance = distance
            ))
                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                .subscribeSingleOnMain { apiResponse, throwable ->
                    when (apiResponse) {
                        is ApiSuccessResponse -> {
                            _vehicleBooked.postValue(true)
                            vehicleId.set(apiResponse.body?.vehicleId)
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
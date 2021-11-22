package com.basil.taxiprepaid.ui.payout

import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleObserver
import com.basil.taxiprepaid.data.remote.ApiErrorResponse
import com.basil.taxiprepaid.data.remote.ApiSuccessResponse
import com.basil.taxiprepaid.data.repository.driver.DriverRepository
import com.basil.taxiprepaid.ui.base.ObservableViewModel
import com.basil.taxiprepaid.utils.subscribeSingleOnMain
import com.basil.taxiprepaid.utils.toCurrencyFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class PayoutViewModel @Inject constructor(
    private val driverRepository: DriverRepository
): ObservableViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    val amount = ObservableField("")

    fun getPayout() {
        disposable.add(
            driverRepository.getPayout()
                .doAfterSuccess { _requestStatus.postValue(APIStatus.Done) }
                .doOnSubscribe{ _requestStatus.postValue(APIStatus.Start) }
                .subscribeSingleOnMain { apiResponse, throwable ->
                    when (apiResponse) {
                        is ApiSuccessResponse -> {
                            amount.set(apiResponse.body.balance.toCurrencyFormat().substring(1))
                            _error.postValue("")
                        }
                        is ApiErrorResponse -> {
                            amount.set("--")
                            _error.postValue(apiResponse.errorMessage)
                        }
                        else -> {
                            amount.set("--")
                            _error.postValue("error")
                        }
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
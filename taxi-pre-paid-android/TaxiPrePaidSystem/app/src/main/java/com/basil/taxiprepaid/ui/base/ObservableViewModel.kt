package com.basil.taxiprepaid.ui.base

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*

open class ObservableViewModel : ViewModel(), Observable {

    fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>
    var _requestStatus: MutableLiveData<APIStatus> = MutableLiveData()
    val requestStatus = _requestStatus.asLiveData()

    var _logOut: MutableLiveData<Boolean> = MutableLiveData()
    val logout = _logOut.asLiveData()
    val _error = MutableLiveData<String>()
    val error = _error.asLiveData()
    val _message: MutableLiveData<String> = MutableLiveData()
    val message = _message.asLiveData()

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    @Suppress("unused")
    fun notifyChange() {
            callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    enum class APIStatus {
        Start,
        Done
    }
}
fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}
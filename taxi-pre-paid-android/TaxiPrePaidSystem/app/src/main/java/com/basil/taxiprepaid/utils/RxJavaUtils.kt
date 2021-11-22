package com.basil.taxiprepaid.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

fun <T> Single<T>.subscribeSingleOnMain(onResult: (T?, Throwable?) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            onResult.invoke(it, null)
        }, {
            Timber.e(it)
            onResult.invoke(null, it)
        })
}
package com.roshnee.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter
import java.util.*

class NetworkStateReceiver : BroadcastReceiver() {
    private var listeners: MutableSet<NetworkStateReceiverListener> = HashSet()
    private var connected: Boolean?
    override fun onReceive(context: Context, intent: Intent) {
        if (intent?.extras == null) return
        connected = isNetworkAvailable(context)
        notifyStateToAll()
    }

    private fun notifyStateToAll() {
        for (listener in listeners) notifyState(listener)
    }

    private fun notifyState(listener: NetworkStateReceiverListener?) {
        if (connected == null || listener == null) return
        if (connected == true) listener.networkAvailable() else listener.networkUnavailable()
    }

    fun addListener(l: NetworkStateReceiverListener) {
        listeners.add(l)
        notifyState(l)
    }

    fun removeListener(l: NetworkStateReceiverListener?) {
        listeners.remove(l)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.state == NetworkInfo.State.CONNECTED
        }
    }

    interface NetworkStateReceiverListener {
        fun networkAvailable()
        fun networkUnavailable()
    }

    init {
        connected = null
    }
}
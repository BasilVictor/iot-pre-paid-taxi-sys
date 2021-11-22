package com.basil.taxiprepaid.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.basil.taxiprepaid.ui.main.MainActivity
import com.basil.taxiprepaid.R
import com.kaopiz.kprogresshud.KProgressHUD
import com.roshnee.broadcastreceiver.NetworkStateReceiver

abstract class BaseActivity :
    AppCompatActivity(),
    NetworkStateReceiver.NetworkStateReceiverListener {
    private var kProgressHUD: KProgressHUD? = null
    private var networkStateReceiver: NetworkStateReceiver = NetworkStateReceiver()
    var connected = false
    fun showCancelButton() {
        supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        }
    }

    fun showBackButton() {
        supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kProgressHUD = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel(getString(R.string.text_wait))
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        networkStateReceiver.addListener(this)
        this.registerReceiver(
            networkStateReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    public override fun onResume() {
        super.onResume()
        connected = isNetworkAvailable(applicationContext!!)
        getBaseObservable().requestStatus.observe(this, {
                when(it){
                    ObservableViewModel.APIStatus.Done -> {
                        if (kProgressHUD?.isShowing == true) {
                            kProgressHUD?.dismiss()
                        }
                    }
                    ObservableViewModel.APIStatus.Start -> {
                        if (kProgressHUD?.isShowing == false) {
                            kProgressHUD?.show()
                        }
                    }
                }
        })

        getBaseObservable().error.observe(this, {
            if (it.equals("error")) {
                Toast.makeText(this, R.string.text_error, Toast.LENGTH_SHORT).show()
            } else if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        getBaseObservable().message.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        getBaseObservable().logout.observe(this,{
            if(it){
                MaterialDialog(this).show {
                    message(R.string.logout_message)
                    cancelable(false)
                    cancelOnTouchOutside(false)
                    positiveButton(R.string.text_ok) {
                        val intent = Intent(this@BaseActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver.removeListener(this)
        unregisterReceiver(networkStateReceiver)
    }

    @SuppressLint("MissingPermission")
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }

    override fun networkAvailable() {
        connected = true
    }

    override fun networkUnavailable() {
        connected = false
    }

    abstract fun getBaseObservable() : ObservableViewModel
}
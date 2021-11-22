package com.basil.taxiprepaid.ui.driverhome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.basil.taxiprepaid.R
import com.basil.taxiprepaid.databinding.ActivityDriverHomeBinding
import com.basil.taxiprepaid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import com.mukesh.OnOtpCompletionListener
import com.afollestad.materialdialogs.customview.getCustomView
import com.basil.taxiprepaid.ui.completedtrips.CompletedTripsActivity
import com.basil.taxiprepaid.ui.main.MainActivity
import com.basil.taxiprepaid.ui.payout.PayoutActivity
import com.basil.taxiprepaid.ui.payout.PayoutViewModel
import com.google.android.material.snackbar.Snackbar
import com.mukesh.OtpView
import com.roshnee.data.repository.PreferenceRepository
import javax.inject.Inject

@AndroidEntryPoint
class DriverHomeActivity : BaseActivity(), OnTripMarkedCompleteListener {
    private lateinit var binding: ActivityDriverHomeBinding
    private val driverHomeViewModel by viewModels<DriverHomeViewModel>()
    var otpEntered: String = ""
    @Inject
    lateinit var preferenceRepository: PreferenceRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewmodel = driverHomeViewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Ongoing Trips"

        driverHomeViewModel.snackbarMessage.observe(this, {
            if(!it.hasBeenHandled) {
                Snackbar.make(binding.uncompletedTripList,
                    it.getMessageIfNotHandled().toString(), Snackbar.LENGTH_SHORT).show()
            }
        })

        driverHomeViewModel.uncompletedTripList.observe(this, {
            driverHomeViewModel._error.postValue("")
            binding.uncompletedTripList.visibility = View.VISIBLE
            binding.uncompletedTripList.adapter =
                UncompletedTripItemAdapter(this@DriverHomeActivity, it, this)
            val dividerItemDecoration =
                DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            binding.uncompletedTripList.addItemDecoration(dividerItemDecoration)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.driver_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.past_trips -> {
                val intent = Intent(this, CompletedTripsActivity::class.java)
                startActivity(intent)
            }
            R.id.payout -> {
                val intent = Intent(this, PayoutActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                preferenceRepository.clear()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        driverHomeViewModel.fetchUncompletedTrips()
    }

    override fun onTripMarkedCompleted(position: Int) {
        val dialog = MaterialDialog(this@DriverHomeActivity).show {
            customView(R.layout.otp_dialog)
            cancelable(false)
            cancelOnTouchOutside(false)
            positiveButton(R.string.complete_trip) {
                if(otpEntered.length==6)
                    driverHomeViewModel.markTripCompleted(position, otpEntered.toInt())
                else
                    Toast.makeText(this@DriverHomeActivity, "Enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
            negativeButton(R.string.cancel){ dismiss() }
        }
        val otpView = dialog.getCustomView().findViewById<OtpView>(R.id.otp_view)
        otpView.setOtpCompletionListener { otp ->
            otpEntered = otp
        }
    }

    override fun getBaseObservable() = driverHomeViewModel

}
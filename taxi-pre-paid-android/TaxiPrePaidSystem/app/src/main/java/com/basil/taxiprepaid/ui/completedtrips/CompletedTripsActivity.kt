package com.basil.taxiprepaid.ui.completedtrips

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.basil.taxiprepaid.databinding.ActivityCompletedTripsBinding
import com.basil.taxiprepaid.ui.base.BaseActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletedTripsActivity : BaseActivity() {
    private lateinit var binding: ActivityCompletedTripsBinding
    private val completedTripsViewModel by viewModels<CompletedTripsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewmodel = completedTripsViewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Completed Trips"
        showBackButton()

        completedTripsViewModel.snackbarMessage.observe(this, {
            if(!it.hasBeenHandled) {
                Snackbar.make(binding.completedTripList,
                    it.getMessageIfNotHandled().toString(), Snackbar.LENGTH_SHORT).show()
            }
        })

        completedTripsViewModel.completedTripList.observe(this, {
            binding.completedTripList.adapter =
                CompletedTripItemAdapter(this@CompletedTripsActivity, it)
            val dividerItemDecoration =
                DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            binding.completedTripList.addItemDecoration(dividerItemDecoration)
        })
    }

    override fun onResume() {
        super.onResume()
        completedTripsViewModel.fetchCompletedTrips()
    }

    override fun getBaseObservable() = completedTripsViewModel
}
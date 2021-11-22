package com.basil.taxiprepaid.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.basil.taxiprepaid.databinding.ActivityMainBinding
import com.basil.taxiprepaid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import com.basil.taxiprepaid.ui.boothbooking.BoothBookingActivity
import com.basil.taxiprepaid.ui.driverhome.DriverHomeActivity

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewmodel = mainViewModel

        mainViewModel.moveToBoothScreen.observe(this, {
            if (it) {
                val intent = Intent(this, BoothBookingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })

        mainViewModel.moveToDriverScreen.observe(this, {
            if (it) {
                val intent = Intent(this, DriverHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })
    }

    override fun getBaseObservable() = mainViewModel
}
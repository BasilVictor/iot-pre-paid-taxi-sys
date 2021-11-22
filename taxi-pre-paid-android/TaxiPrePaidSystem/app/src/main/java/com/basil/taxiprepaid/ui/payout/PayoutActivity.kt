package com.basil.taxiprepaid.ui.payout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.basil.taxiprepaid.databinding.ActivityCompletedTripsBinding
import com.basil.taxiprepaid.databinding.ActivityPayoutBinding
import com.basil.taxiprepaid.ui.base.BaseActivity
import com.basil.taxiprepaid.ui.base.ObservableViewModel
import com.basil.taxiprepaid.ui.completedtrips.CompletedTripsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayoutActivity : BaseActivity() {
    private lateinit var binding: ActivityPayoutBinding
    private val payoutViewModel by viewModels<PayoutViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewmodel = payoutViewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Payout"
        showBackButton()
    }

    override fun onResume() {
        super.onResume()
        payoutViewModel.getPayout()
    }

    override fun getBaseObservable() = payoutViewModel
}
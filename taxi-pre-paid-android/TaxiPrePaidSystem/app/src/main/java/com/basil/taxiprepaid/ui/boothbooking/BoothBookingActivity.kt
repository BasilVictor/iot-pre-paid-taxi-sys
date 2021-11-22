package com.basil.taxiprepaid.ui.boothbooking

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.basil.taxiprepaid.R
import com.basil.taxiprepaid.databinding.ActivityBoothBookingBinding
import com.basil.taxiprepaid.ui.base.BaseActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class BoothBookingActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private lateinit var binding: ActivityBoothBookingBinding
    private val boothBookingViewModel by viewModels<BoothBookingViewModel>()
    private lateinit var mMap: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoothBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewmodel = boothBookingViewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Booking Counter"

        val fields = listOf(Place.Field.ADDRESS)

        boothBookingViewModel.moveToAutoCompleteScreen.observe(this, {
            if(it) {
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setCountry("IN")
                    .setHint("Destination")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
        })

        boothBookingViewModel.cost.observe(this, {
            val dialog = MaterialDialog(this).show {
                customView(R.layout.confirmation_dialog)
                cancelable(false)
                cancelOnTouchOutside(false)
                positiveButton(R.string.confirm_booking) {
                    boothBookingViewModel.confirmTaxi()
                }
                negativeButton(R.string.cancel){ dismiss() }
            }
            dialog.getCustomView().findViewById<TextView>(R.id.title).text = getString(R.string.journey_cost)
            dialog.getCustomView().findViewById<TextView>(R.id.content).text = it.toString()
            dialog.getCustomView().findViewById<TextView>(R.id.rupee_symbol).visibility = View.VISIBLE
        })

        boothBookingViewModel.vehicleBooked.observe(this, {
            if(it) {
                val dialog = MaterialDialog(this).show {
                    customView(R.layout.confirmation_dialog)
                    cancelable(false)
                    cancelOnTouchOutside(false)
                    positiveButton(R.string.close) {
                        mMap.clear()
                        binding.tietPassengerName.text = null
                        binding.tietPhone.text = null
                        binding.tietDestination.text = null
                    }
                }
                dialog.getCustomView().findViewById<TextView>(R.id.title).text = getString(R.string.vehicle_number)
                dialog.getCustomView().findViewById<TextView>(R.id.content).text =
                    boothBookingViewModel.vehicleId.get().toString()
                dialog.getCustomView().findViewById<TextView>(R.id.rupee_symbol).visibility = View.GONE
            }
        })

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun getBaseObservable() = boothBookingViewModel

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerDragListener(this)
    }

    override fun onMarkerDrag(p0: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        val geocoder = Geocoder(this@BoothBookingActivity, Locale.ENGLISH)
        try {
            val addresses = geocoder.getFromLocation(
                marker.position.latitude,
                marker.position.longitude,
                1
            )
            if (addresses.size > 0) {
                binding.tietDestination.setText(addresses[0].getAddressLine(0))
            }
            boothBookingViewModel.destLat = marker.position.latitude.toFloat()
            boothBookingViewModel.destLng = marker.position.longitude.toFloat()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this@BoothBookingActivity, "Failed to get address", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMarkerDragStart(p0: Marker) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val geocoder = Geocoder(this)
                        val place = Autocomplete.getPlaceFromIntent(data)
                        binding.tietDestination.setText(place.address)
                        val addressLatLng: List<Address>? = geocoder.getFromLocationName(place.address, 1)
                        val destinationLatLng =
                            LatLng(addressLatLng!![0].latitude, addressLatLng[0].longitude)
                        mMap.clear()
                        mMap.addMarker(
                            MarkerOptions()
                                .position(destinationLatLng)
                                .draggable(true)
                        )
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                destinationLatLng,
                                15.8f
                            )
                        )
                        boothBookingViewModel.destLat = addressLatLng[0].latitude.toFloat()
                        boothBookingViewModel.destLng = addressLatLng[0].longitude.toFloat()
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                }
                Activity.RESULT_CANCELED -> {
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
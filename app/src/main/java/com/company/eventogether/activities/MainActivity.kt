package com.company.eventogether.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.eventogether.AppSettings.EVENT_UPDATE_PERIOD_IN_MILLIS
import com.company.eventogether.R
import com.company.eventogether.adapters.HorizontalEventListAdapter
import com.company.eventogether.databinding.ActivityMainBinding
import com.company.eventogether.fragments.EventListFragment
import com.company.eventogether.helpclasses.SharedPreferencesHelper
import com.company.eventogether.helpclasses.SharedPreferencesHelper.TYPE_LONG
import com.company.eventogether.helpclasses.SharedPreferencesHelper.TYPE_STRING
import com.company.eventogether.model.EventDTO
import com.company.eventogether.model.EventType
import com.company.eventogether.model.LocationDTO
import com.company.eventogether.viewmodels.EventViewModel
import com.company.eventogether.viewmodels.LocationViewModel
import com.company.eventogether.viewmodels.UserProfileViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: HorizontalEventListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val userProfileViewModel: UserProfileViewModel by viewModel()
    private val eventViewModel: EventViewModel by viewModel()
    private val locationViewModel: LocationViewModel by viewModel()
    private val sharedPreferencesHelper: SharedPreferencesHelper by inject()

    companion object {
        private const val TAG = "MainActivity"
        private const val LOCATION_THRESHOLD = 1000
        private var forceLocationUpdate = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                supportFragmentManager.popBackStack()
            }
        })

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                return@addOnBackStackChangedListener
            }

            when (supportFragmentManager.fragments.last()) {

                is EventListFragment -> {

                    findViewById<ImageView>(R.id.toolbarImage).setOnClickListener {
                        supportFragmentManager.popBackStack()
                    }
                }
            }
        }

        manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.horizontalRecyclerView.layoutManager = manager
        adapter = HorizontalEventListAdapter(eventViewModel)
        binding.horizontalRecyclerView.adapter = adapter

        binding.btnEvents.setOnClickListener {
            callEventActivity()
        }

        binding.btnEventsForward.setOnClickListener {
            callEventActivity()
        }

        binding.btnChat.setOnClickListener {
            callChatActivity()
        }

        binding.btnChatForward.setOnClickListener {
            callChatActivity()
        }

        binding.btnScan.setOnClickListener {

        }

        binding.btnScanForward.setOnClickListener {

        }

        setObservers()
        setCustomToolbar()
        checkLastKnownLocation()
    }

    private fun setObservers() {

        userProfileViewModel.startSignInAction.observe(this) { boolean ->

            if (boolean) {
                callSignInActivity()
                userProfileViewModel.startSignInAction.value = false
            }
        }

        userProfileViewModel.isSignedInSuccess.observe(this) { boolean ->

            if (boolean) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.login_succes_text,
                    Toast.LENGTH_SHORT
                ).show()

                userProfileViewModel.isSignedInSuccess.value = false
            }
        }

        userProfileViewModel.isSignedInError.observe(this) { boolean ->

            if (boolean) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.login_error_text,
                    Toast.LENGTH_LONG
                ).show()

                userProfileViewModel.signOutOnError(this@MainActivity)
                userProfileViewModel.isSignedInError.value = false
            }
        }

        userProfileViewModel.isSignedOutSuccess.observe(this) { boolean ->

            if (boolean) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.logout_succes_text,
                    Toast.LENGTH_SHORT
                ).show()

                setCustomToolbar()
                userProfileViewModel.isSignedOutSuccess.value = false
            }
        }

        eventViewModel.eventObservable.observe(this) { event ->

            if (event != null) {
                callEventActivity(event)
            }
        }

        locationViewModel.isLocationRequestTriggered.observe(this) { boolean ->

            if (boolean) {

                val location = locationViewModel.getCurrentLocation()
                val timeInMillis = Calendar.getInstance().timeInMillis

                if (location != null) {
                    updateEvents(location, timeInMillis)
                    Log.d(TAG, "Forced event list update triggered!")
                }

                locationViewModel.isLocationRequestTriggered.value = false
            }
        }

        locationViewModel.currentLocationObservable.observe(this) { location ->

            if (location != null) {

                locationViewModel.updateLocation(location)

                sharedPreferencesHelper.savePreferences(
                    this,
                    "UserData",
                    "FbLocationKey",
                    stringValue = location.fbKey
                )

                checkLastEventUpdate()
            }
        }
    }

    private fun setCustomToolbar() {

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.main_toolbar)

        userProfileViewModel.setCustomMenu(
            this@MainActivity, findViewById(R.id.profileIcon), findViewById(R.id.menuIcon)
        )
    }

    private fun updateEvents(location: LocationDTO, timeInMillis: Long) {

        eventViewModel.updateEvents(this, EventType.All, location) { boolean ->

            if (boolean == true) {

                sharedPreferencesHelper.savePreferences(
                    this,
                    "UserData",
                    "LastUpdateTimeInMillis",
                    longValue = timeInMillis
                )
            }
        }
    }

    private fun checkLastKnownLocation() {

        val fbLocationKey = sharedPreferencesHelper.retrievePreferences(
            this,
            "UserData",
            "FbLocationKey",
            TYPE_STRING
        )

        if (fbLocationKey != null) {

            locationViewModel.loadLastLocation(fbLocationKey as String) { lastKnownLocation ->

                if (lastKnownLocation != null) {

                    fusedLocationClient.lastLocation.addOnSuccessListener { fusedLocation ->

                        if (fusedLocation != null) {

                            Location("").apply {
                                lastKnownLocation.latitude.let {
                                    if (it != null) {
                                        this.latitude = it
                                    }
                                }
                                lastKnownLocation.longitude.let {
                                    if (it != null) {
                                        this.longitude = it
                                    }
                                }
                            }.let {
                                fusedLocation.distanceTo(it).let { distance ->

                                    if (distance > LOCATION_THRESHOLD) {
                                        checkLocationPermission()
                                    } else {
                                        locationViewModel.currentLocationObservable.value =
                                            lastKnownLocation
                                        Log.d(
                                            TAG,
                                            "Last known location is still accurate. Location: ${lastKnownLocation.formattedAddress}, deviation: $distance"
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    checkLocationPermission()
                }
            }
        } else {
            checkLocationPermission()
        }
    }

    private fun checkLastEventUpdate() {

        (sharedPreferencesHelper.retrievePreferences(
            this,
            "UserData",
            "LastUpdateTimeInMillis",
            TYPE_LONG
        ) as Long).let { lastUpdateTimeInMillis ->

            val timeInMillis = Calendar.getInstance().timeInMillis
            val timeToUpdate = timeInMillis.plus(EVENT_UPDATE_PERIOD_IN_MILLIS)

            if (lastUpdateTimeInMillis >= timeToUpdate || lastUpdateTimeInMillis == 0L || forceLocationUpdate) {

                locationViewModel.getCurrentLocation().let { location ->

                    if (location != null) {
                        updateEvents(location, timeInMillis)
                    }
                }
            }
        }
    }

    private fun checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            makePermissionsRequest()
        } else {

            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

            Log.d(TAG, "Play services connected: ${resultCode == ConnectionResult.SUCCESS}")
            Log.d(TAG, "Geocoder availability: ${Geocoder.isPresent()}")

            googleApiAvailability
                .checkApiAvailability(fusedLocationClient)
                .onSuccessTask {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { fusedLocation ->

                            if (fusedLocation != null) {
                                val latLong = "${fusedLocation.latitude}, ${fusedLocation.longitude}"
                                locationViewModel.getGoogleLocationByLatLong(latLong)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "An exception occurred while retrieving location data: $e")
                        }
                }
        }
    }

    private fun makePermissionsRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        R.string.text_location_rights,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    checkLocationPermission()
                }
            }
        }
    }

    private fun callEventActivity(event: EventDTO? = null) {

        userProfileViewModel.getCurrentUser().let {
            if (it != null) {
                val intent = Intent(this@MainActivity, EventActivity::class.java).apply {
                    this.putExtra("Event", event)
                }
                startActivity(intent)
            } else {
                callSignInActivity()
            }
        }
    }

    private fun callChatActivity() {

        userProfileViewModel.getCurrentUser().let {
            if (it != null) {
                val intent = Intent(this@MainActivity, ChatActivity::class.java)
                startActivity(intent)
            } else {
                callSignInActivity()
            }
        }
    }

    private fun callSignInActivity() {
        val intent = Intent(this@MainActivity, SignInActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        adapter.stopListening()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}
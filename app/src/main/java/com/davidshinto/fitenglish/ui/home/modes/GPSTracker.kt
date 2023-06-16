package com.davidshinto.fitenglish.ui.home.modes

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.utils.GameHelper
import com.google.android.gms.location.*


//later need to refactor it to the service so it will work in background
class GPSTracker : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var distance = 0.0
    private var previousLocation: Location? = null
    private lateinit var gameHelper: GameHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpstracker)

//        newer one but doesn't work for my OREO :(
//        gameHelper = intent.parcelable("GAME_HELPER")!!

        gameHelper = intent.getParcelableExtra("GAME_HELPER")!!

        if (gameHelper.nowDistance + gameHelper.breakDistance > gameHelper.totalDistance) {
            gameHelper.breakDistance = gameHelper.totalDistance - gameHelper.nowDistance
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 2f
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (previousLocation != null) {
                        distance += calculateDistance(location, previousLocation!!)
                        val formattedDistance = "Distance: " + distance.toInt().toString()+" m"
                        findViewById<TextView>(R.id.tvDistancePassed).text = formattedDistance
                        val progressPercentage =
                            (distance / gameHelper.breakDistance * 100).toInt()
                        findViewById<ProgressBar>(R.id.progressBar).progress = progressPercentage
                    }
                    previousLocation = location
                }
                if (distance >= gameHelper.breakDistance) {
                    Toast.makeText(applicationContext,"Finished " + gameHelper.breakDistance + " metres",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra("GAME_HELPER", gameHelper)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun calculateDistance(location1: Location, location2: Location): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude, results
        )
        return results[0]
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onDestroy() {
        findViewById<ProgressBar>(R.id.progressBar).progress = 0
        super.onDestroy()
    }
}
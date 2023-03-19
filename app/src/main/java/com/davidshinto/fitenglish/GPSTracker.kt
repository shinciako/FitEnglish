package com.davidshinto.fitenglish

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class GPSTracker : AppCompatActivity(), SensorEventListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var distance = 0.0
    private var previousLocation: Location? = null
    private var lastAccelerometerData: FloatArray? = null
    private var lastAccelerometerTimestamp: Long = 0
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpstracker)

        game = intent.getParcelableExtra("gameOutput")!!

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

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
                        findViewById<TextView>(R.id.tvDistancePassed).text = distance.toString()
                    }
                    previousLocation = location
                }
                if (distance >= game.distanceAfterTest) {
                    val intent = Intent(this@GPSTracker, FlashGameActivity::class.java).apply {
                        putExtra("GAME", game)
                    }
                    startActivity(intent)
                }
            }
        }
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

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()

        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            lastAccelerometerData = event.values.clone()
            lastAccelerometerTimestamp = event.timestamp
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

    private fun calculateDistance(location1: Location, location2: Location): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude, results
        )
        return results[0]
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}
package com.strategickaos.placebook

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapsActivity : AppCompatActivity(), OnMapReadyCallback
{

	private lateinit var mMap: GoogleMap
	private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_maps)
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		val mapFragment = supportFragmentManager
				.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)
		setUpLocationClient()
	}

	override fun onMapReady(googleMap: GoogleMap)
	{
		mMap = googleMap
		getCurrentLocation()
	}

	private fun setUpLocationClient(){
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
	}

	private fun requestLocationPermission(){
		ActivityCompat.requestPermissions(this,
			arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
		REQUEST_LOCATION)
	}


	companion object{
		private const val REQUEST_LOCATION = 1
		private const val TAG = "MapsActivity"
	}

	private fun getCurrentLocation(){
		if(ActivityCompat.checkSelfPermission(this,
			android.Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED){
			requestLocationPermission()
		}else{
				mMap.isMyLocationEnabled = true
				fusedLocationProviderClient.lastLocation.addOnCompleteListener{
				val location = it.result
				if(location != null){
					val latLng = LatLng(location.latitude, location.longitude)
					val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
					mMap.moveCamera(update)
				}else{
					Log.e(TAG,"No Location found")
				}
			}
		}
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray) {
		if(requestCode == REQUEST_LOCATION){
			if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
				getCurrentLocation()
			}else{
				Log.e(TAG, "Location permission denied")
			}
		}

	}
}
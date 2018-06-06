package com.kevingt.carlocator.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kevingt.carlocator.R
import com.kevingt.carlocator.util.UiUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var viewModel: MapsViewModel
    private lateinit var fabPunch: FloatingActionButton
    private lateinit var tvTime: TextView
    private lateinit var tvAddress: TextView
    private lateinit var rxPermissions: RxPermissions
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mMap: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        rxPermissions = RxPermissions(this)
        compositeDisposable = CompositeDisposable()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fabPunch = findViewById(R.id.fab_main_punch)
        fabPunch.setOnClickListener(this)
        tvTime = findViewById(R.id.tv_main_time)
        tvAddress = findViewById(R.id.tv_main_address)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = MapsViewModel(this)
        updateView(viewModel.getLat(), viewModel.getLng(), viewModel.time.value!!)
    }

    @SuppressLint("MissingPermission")
    override fun onClick(v: View?) {
        if (v?.id != R.id.fab_main_punch) return
        compositeDisposable.add(rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe({
                    if (it) {
                        mMap?.isMyLocationEnabled = true
                        fusedLocationClient.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    if (location != null) {
                                        updateLocation(location.latitude, location.longitude)
                                    } else {
                                        Toast.makeText(this, R.string.main_cannot_get_location, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        //TODO: add error listener
                    } else {
                        Toast.makeText(this, R.string.main_need_location_permission, Toast.LENGTH_SHORT).show()
                    }
                }))
    }

    private fun updateLocation(lat: Double?, lng: Double?) {
        val currentTime = UiUtil.getCurrentTime()
        viewModel.setLat(lat!!)
        viewModel.setLng(lng!!)
        viewModel.setTime(currentTime)
        updateView(lat, lng, currentTime)
    }

    private fun updateView(lat: Double?, lng: Double?, time: String) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address: List<Address> = geoCoder.getFromLocation(lat!!, lng!!, 1)
        //val address: List<Address> = geoCoder.getFromLocation(lat!!, lng!!, 1)
        if (time.isEmpty()) {
            tvTime.text = getString(R.string.main_time_text, getString(R.string.main_no_info))
            tvAddress.text = getString(R.string.main_click_hint)
            return
        }
        tvTime.text = getString(R.string.main_time_text, time)
        tvAddress.text = if (address.isEmpty()) {
            getString(R.string.main_no_matched_address)
        } else {
            UiUtil.parseAddress(address[0])
        }
        drawMarker(LatLng(viewModel.getLat(), viewModel.getLng()), true)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap?.isMyLocationEnabled = true
        }

        // Add previous location marker
        drawMarker(LatLng(viewModel.getLat(), viewModel.getLng()), false)
    }

    private fun drawMarker(location: LatLng, clearMarker: Boolean) {
        if (clearMarker) {
            marker?.remove()
        }
        marker = mMap?.addMarker(MarkerOptions().position(location).title(getString(R.string.main_map_marker_name)))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17.toFloat()))
    }

}

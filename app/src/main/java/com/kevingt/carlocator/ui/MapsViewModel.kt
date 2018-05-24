package com.kevingt.carlocator.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.kevingt.carlocator.data.AppPreferenceManager

class MapsViewModel(context: Context) : ViewModel() {

    private val preferenceManager = AppPreferenceManager.getInstance(context)

    var time = MutableLiveData<String>()
    private var lat = MutableLiveData<Double>()
    private var lng = MutableLiveData<Double>()

    init {
        time.value = preferenceManager.time
        lat.value = preferenceManager.lat
        lng.value = preferenceManager.lng
    }

    fun setTime(time: String) {
        this.time.value = time
        preferenceManager.time = time
    }

    fun setLat(lat: Double) {
        this.lat.value = lat
        preferenceManager.lat = lat
    }

    fun setLng(lng: Double) {
        this.lng.value = lng
        preferenceManager.lng = lng
    }

    fun getLat() = preferenceManager.lat
    fun getLng() = preferenceManager.lng
}
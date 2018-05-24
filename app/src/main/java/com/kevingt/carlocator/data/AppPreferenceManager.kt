package com.kevingt.carlocator.data

import android.content.Context
import android.content.SharedPreferences

class AppPreferenceManager private constructor(context: Context) : PreferenceManager {

    private var sharedPreferences: SharedPreferences

    companion object {
        const val FILENAME: String = "CarLocator"
        const val KEY_LAT: String = "LAT"
        const val KEY_LNG: String = "LNG"
        const val KEY_TIME: String = "TIME"

        private var instance: AppPreferenceManager? = null
        fun getInstance(context: Context): AppPreferenceManager {
            if (instance == null) instance = AppPreferenceManager(context)
            return instance!!
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    }

    override var lat: Double
        set(value) {
            sharedPreferences.edit().putString(KEY_LAT, value.toString()).apply()
        }
        get() = sharedPreferences.getString(KEY_LAT, "23.973875").toDouble()

    override var lng: Double
        set(value) {
            sharedPreferences.edit().putString(KEY_LNG, value.toString()).apply()
        }
        get() = sharedPreferences.getString(KEY_LNG, "120.982024").toDouble()

    override var time: String
        set(value) {
            sharedPreferences.edit().putString(KEY_TIME, value).apply()
        }
        get() = sharedPreferences.getString(KEY_TIME, "")
}
package com.kevingt.carlocator.util

import android.location.Address
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

class UiUtil {
    companion object {
        fun getCurrentTime(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return formatter.format(Date())
        }

        fun parseAddress(address: Address): String {
            val builder = StringBuilder()
            builder.append(address.adminArea).append(address.locality)
            if (!TextUtils.isDigitsOnly(address.featureName)) {
                builder.append(address.featureName)
                return builder.toString()
            }
            builder.append(address.thoroughfare)
            if (address.subThoroughfare != null) {
                builder.append(address.subThoroughfare)
            }
            return builder.toString()
        }
    }
}
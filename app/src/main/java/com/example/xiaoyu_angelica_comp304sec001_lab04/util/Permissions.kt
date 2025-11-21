package com.example.xiaoyu_angelica_comp304sec001_lab04.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Permissions {
    private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private const val BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION

    fun hasFineLocation(context: Context) =
        ContextCompat.checkSelfPermission(context, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun hasBackgroundLocation(context: Context) =
        ContextCompat.checkSelfPermission(context, BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
}
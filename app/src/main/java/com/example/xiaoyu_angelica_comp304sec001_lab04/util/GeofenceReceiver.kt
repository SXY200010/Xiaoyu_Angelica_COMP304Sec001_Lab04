package com.example.xiaoyu_angelica_comp304sec001_lab04.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return
        val transition = event.geofenceTransition
        val msg = when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Entered geofence"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Exited geofence"
            else -> "Geofence transition: $transition"
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
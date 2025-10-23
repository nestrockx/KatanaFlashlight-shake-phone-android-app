package com.wegielek.katanaflashlight.data

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.wegielek.katanaflashlight.domain.ServiceController
import com.wegielek.katanaflashlight.service.FlashlightForegroundService

class AndroidServiceController(
    private val context: Context,
) : ServiceController {
    override fun startFlashlightService() {
        val intent = Intent(context, FlashlightForegroundService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopFlashlightService() {
        val intent = Intent(context, FlashlightForegroundService::class.java)
        context.stopService(intent)
    }

    override fun isFlashlightServiceRunning(): Boolean {
        try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val services = manager.getRunningServices(Int.MAX_VALUE)
            for (serviceInfo in services) {
                if (serviceInfo.service.className == FlashlightForegroundService::class.java.name) {
                    return true
                }
            }
        } catch (t: Throwable) {
            // ignore, return flagged
        }
        return false
    }
}

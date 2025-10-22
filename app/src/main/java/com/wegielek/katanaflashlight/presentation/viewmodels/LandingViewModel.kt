package com.wegielek.katanaflashlight.presentation.viewmodels

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.CAMERA_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.wegielek.katanaflashlight.FlashlightForegroundService
import com.wegielek.katanaflashlight.Prefs
import com.wegielek.katanaflashlight.R

class LandingViewModel : ViewModel() {
    var cameraManager: CameraManager? = null
    var cameraId: String? = null
    var foregroundService: Intent? = null

    fun init(context: Context) {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager?
            try {
                cameraId = cameraManager?.cameraIdList?.get(0)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            Toast
                .makeText(
                    context,
                    context.getString(R.string.flashlight_not_available),
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }

    fun checkNotificationPermission(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    fun checkCameraPermission(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    fun startService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isMyServiceRunning(context, FlashlightForegroundService::class.java)) {
                foregroundService = Intent(context, FlashlightForegroundService::class.java)
                foregroundService?.let { ContextCompat.startForegroundService(context, it) }
            }
            return
        }
        if (!isMyServiceRunning(context, FlashlightForegroundService::class.java)) {
            foregroundService = Intent(context, FlashlightForegroundService::class.java)
            foregroundService?.let { ContextCompat.startForegroundService(context, it) }
        }
    }

    fun onIntensityChange(
        context: Context,
        intensity: Float,
    ) {
        val values = listOf(9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39)
        Prefs.setThreshold(context, values[intensity.toInt()].toFloat())
    }

    fun onStrengthChange(
        context: Context,
        strength: Int,
    ) {
        Prefs.setStrength(context, strength)
        updateFlashlight(context, false)
    }

    fun isMyServiceRunning(
        context: Context,
        serviceClass: Class<*>,
    ): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.runningAppProcesses
        if (runningServices != null) {
            for (processInfo in runningServices) {
                if (processInfo.pkgList != null) {
                    for (packageName in processInfo.pkgList) {
                        if (serviceClass.name == packageName) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun hasFlashlightStrengthLevels(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val x: Int? =
                cameraManager?.getCameraCharacteristics(cameraId!!)?.get(
                    CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL,
                )
            if (x != null) {
                if (x > 1) {
                    return true
                }
            }
        }
        return false
    }

    fun getFlashlightMaximumStrengthLevel(context: Context): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val x: Int? = cameraManager?.getCameraCharacteristics(cameraId!!)?.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL)
            Prefs.setMaximumStrength(context, x!!)
            x
        } else {
            1
        }

    fun updateFlashlight(
        context: Context,
        toggle: Boolean,
    ) {
        if (toggle) {
            if (!Prefs.getFlashOn(context)) {
                try {
                    if (hasFlashlightStrengthLevels()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            try {
                                cameraManager?.turnOnTorchWithStrengthLevel(
                                    cameraId!!,
                                    Prefs.getStrength(context),
                                )
                            } catch (e: IllegalArgumentException) {
                                cameraManager?.setTorchMode(cameraId!!, true)
                                e.printStackTrace()
                            }
                        }
                    } else {
                        cameraManager?.setTorchMode(cameraId!!, true)
                    }
                    Prefs.setFlashOn(context, !Prefs.getFlashOn(context))
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    cameraManager?.setTorchMode(cameraId!!, false)
                    Prefs.setFlashOn(context, !Prefs.getFlashOn(context))
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        } else if (Prefs.getFlashOn(context)) {
            try {
                if (hasFlashlightStrengthLevels()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            cameraManager?.turnOnTorchWithStrengthLevel(
                                cameraId!!,
                                Prefs.getStrength(context),
                            )
                        } catch (e: IllegalArgumentException) {
                            cameraManager?.setTorchMode(cameraId!!, true)
                            e.printStackTrace()
                        }
                    }
                } else {
                    cameraManager?.setTorchMode(cameraId!!, true)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    fun onVibrationSwitch(
        context: Context,
        boolean: Boolean,
    ) {
        Prefs.setVibrationOn(context, boolean)
    }

    fun onKatanaSwitch(
        context: Context,
        value: Boolean,
    ) {
        Prefs.setKatanaOn(context, value)
    }
}

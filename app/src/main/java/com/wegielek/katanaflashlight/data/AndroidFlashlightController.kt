package com.wegielek.katanaflashlight.data

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import com.wegielek.katanaflashlight.Prefs
import com.wegielek.katanaflashlight.domain.FlashlightController

class AndroidFlashlightController(
    private val context: Context,
) : FlashlightController {
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    override fun initialize() {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager?.cameraIdList?.firstOrNull()
    }

    override fun hasFlashlight(): Boolean = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    override fun hasStrengthLevels(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val level =
                cameraManager
                    ?.getCameraCharacteristics(cameraId!!)
                    ?.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL)
            return level != null && level > 1
        }
        return false
    }

    override fun getMaxStrengthLevel(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraManager
                ?.getCameraCharacteristics(cameraId!!)
                ?.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL) ?: 1
        } else {
            1
        }

    override fun toggleFlashlight() {
        val flashOn = Prefs.getFlashOn(context)
        cameraManager?.setTorchMode(cameraId!!, !flashOn)
        Prefs.setFlashOn(context, !flashOn)
    }

    override fun setStrength(level: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraManager?.turnOnTorchWithStrengthLevel(cameraId!!, level)
        }
    }
}

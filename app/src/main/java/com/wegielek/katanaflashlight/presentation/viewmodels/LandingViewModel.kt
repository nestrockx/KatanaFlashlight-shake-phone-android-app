package com.wegielek.katanaflashlight.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.wegielek.katanaflashlight.domain.FlashlightController
import com.wegielek.katanaflashlight.domain.PermissionChecker
import com.wegielek.katanaflashlight.domain.PrefsRepository
import com.wegielek.katanaflashlight.domain.ServiceController

class LandingViewModel(
    private val flashlightController: FlashlightController,
    private val prefs: PrefsRepository,
    private val permissions: PermissionChecker,
    private val service: ServiceController,
) : ViewModel() {
    fun startService() {
        service.startFlashlightService()
    }

    fun isServiceRunning(): Boolean = service.isFlashlightServiceRunning()

    fun stopService() {
        service.stopFlashlightService()
    }

    fun initialize() {
        flashlightController.initialize()
    }

    fun canUseFlashlight(): Boolean = flashlightController.hasFlashlight()

    fun toggleFlashlight() {
        flashlightController.toggleFlashlight()
    }

    fun onStrengthChange(strength: Int) {
        prefs.strength = strength
        flashlightController.setStrength(strength)
    }

    fun onVibrationSwitch(enabled: Boolean) {
        prefs.vibrationOn = enabled
    }

    fun onKatanaSwitch(enabled: Boolean) {
        prefs.katanaOn = enabled
    }

    fun hasStrengthLevels(): Boolean = flashlightController.hasStrengthLevels()

    fun getMaxStrengthLevel(): Int = flashlightController.getMaxStrengthLevel()

    fun hasCameraPermission(): Boolean = permissions.hasCameraPermission()

    fun hasNotificationPermission(): Boolean = permissions.hasNotificationPermission()
}

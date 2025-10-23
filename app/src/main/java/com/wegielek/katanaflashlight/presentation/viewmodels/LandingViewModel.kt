package com.wegielek.katanaflashlight.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.katanaflashlight.NewPrefs
import com.wegielek.katanaflashlight.NewPrefs.flashOn
import com.wegielek.katanaflashlight.domain.FlashlightController
import com.wegielek.katanaflashlight.domain.PermissionChecker
import com.wegielek.katanaflashlight.domain.ServiceController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LandingViewModel(
    private val appContext: Context,
    private val flashlightController: FlashlightController,
    private val permissions: PermissionChecker,
    private val service: ServiceController,
) : ViewModel() {
    var hasStrengthLevels = MutableStateFlow(false)
        private set
    var maxStrengthLevel = MutableStateFlow(1)
        private set
    var hasCameraPermission = MutableStateFlow(false)
        private set
    var hasNotificationPermission = MutableStateFlow(false)
        private set

    fun setHasCameraPermission(value: Boolean) {
        hasCameraPermission.value = value
    }

    fun setHasNotificationPermission(value: Boolean) {
        hasNotificationPermission.value = value
    }

    private fun hasStrengthLevels(): Boolean = flashlightController.hasStrengthLevels()

    private fun getMaxStrengthLevel(): Int = flashlightController.getMaxStrengthLevel()

    fun hasCameraPermission(): Boolean = permissions.hasCameraPermission()

    fun hasNotificationPermission(): Boolean = permissions.hasNotificationPermission()

    fun startService() {
        viewModelScope.launch {
            NewPrefs.setKatanaOn(appContext, true)
            if (!isServiceRunning()) {
                service.startFlashlightService()
            }
        }
    }

    fun isServiceRunning(): Boolean = service.isFlashlightServiceRunning()

    fun stopService() {
        viewModelScope.launch {
            NewPrefs.setKatanaOn(appContext, false)
            if (isServiceRunning()) {
                service.stopFlashlightService()
            }
        }
    }

    fun initialize() {
        viewModelScope.launch {
            flashlightController.initialize()
            hasStrengthLevels.value = hasStrengthLevels()
            maxStrengthLevel.value = getMaxStrengthLevel()
            hasCameraPermission.value = hasCameraPermission()
            hasNotificationPermission.value = hasNotificationPermission()
            NewPrefs.setKatanaOn(appContext, isServiceRunning())
        }
    }

    fun canUseFlashlight(): Boolean = flashlightController.hasFlashlight()

    fun toggleFlashlight() {
        viewModelScope.launch {
            val flashOn = appContext.flashOn.first()
            flashlightController.toggleFlashlight(!flashOn)
            NewPrefs.setFlashOn(appContext, !flashOn)
        }
    }

    fun onStrengthChange(strength: Int) {
        viewModelScope.launch {
            NewPrefs.setStrength(appContext, strength)
            if (appContext.flashOn.first()) {
                flashlightController.setStrength(strength)
            }
        }
    }

    fun onSensitivityChange(sensitivity: Float) {
        viewModelScope.launch {
            NewPrefs.setSensitivity(appContext, sensitivity)
        }
    }

    fun onVibrationSwitch(enabled: Boolean) {
        viewModelScope.launch {
            NewPrefs.setVibrationOn(appContext, enabled)
        }
    }

    fun onKatanaSwitch(enabled: Boolean) {
        if (enabled) {
            startService()
        } else {
            stopService()
        }
    }
}

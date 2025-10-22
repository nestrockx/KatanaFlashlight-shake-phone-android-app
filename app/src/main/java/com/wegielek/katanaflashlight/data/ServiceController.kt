package com.wegielek.katanaflashlight.data

interface ServiceController {
    fun startFlashlightService()

    fun stopFlashlightService()

    fun isFlashlightServiceRunning(): Boolean
}

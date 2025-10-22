package com.wegielek.katanaflashlight.domain

interface ServiceController {
    fun startFlashlightService()

    fun stopFlashlightService()

    fun isFlashlightServiceRunning(): Boolean
}

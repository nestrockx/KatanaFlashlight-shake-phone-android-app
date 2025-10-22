package com.wegielek.katanaflashlight.domain

interface FlashlightController {
    fun initialize()

    fun hasFlashlight(): Boolean

    fun toggleFlashlight()

    fun setStrength(level: Int)

    fun getMaxStrengthLevel(): Int

    fun hasStrengthLevels(): Boolean
}

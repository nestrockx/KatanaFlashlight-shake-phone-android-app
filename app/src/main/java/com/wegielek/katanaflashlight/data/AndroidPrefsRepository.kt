package com.wegielek.katanaflashlight.data

import android.content.Context
import com.wegielek.katanaflashlight.Prefs
import com.wegielek.katanaflashlight.domain.PrefsRepository

class AndroidPrefsRepository(
    private val context: Context,
) : PrefsRepository {
    override var flashOn: Boolean
        get() = Prefs.getFlashOn(context)
        set(value) = Prefs.setFlashOn(context, value)

    override var strength: Int
        get() = Prefs.getStrength(context)
        set(value) = Prefs.setStrength(context, value)

    override var vibrationOn: Boolean
        get() = Prefs.getVibrationOn(context)
        set(value) = Prefs.setVibrationOn(context, value)

    override var katanaOn: Boolean
        get() = Prefs.getKatanaOn(context)
        set(value) = Prefs.setKatanaOn(context, value)

    override fun setThreshold(threshold: Float) {
        Prefs.setThreshold(context, threshold)
    }
}

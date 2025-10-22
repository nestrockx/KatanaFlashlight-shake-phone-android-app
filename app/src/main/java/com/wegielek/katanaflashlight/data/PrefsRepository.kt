package com.wegielek.katanaflashlight.data

interface PrefsRepository {
    var flashOn: Boolean
    var strength: Int
    var vibrationOn: Boolean
    var katanaOn: Boolean

    fun setThreshold(threshold: Float)
}

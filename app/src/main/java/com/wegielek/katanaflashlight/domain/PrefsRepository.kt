package com.wegielek.katanaflashlight.domain

interface PrefsRepository {
    var flashOn: Boolean
    var strength: Int
    var vibrationOn: Boolean
    var katanaOn: Boolean

    fun setThreshold(threshold: Float)
}

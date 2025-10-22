package com.wegielek.katanaflashlight.data

// PermissionChecker.kt
interface PermissionChecker {
    fun hasCameraPermission(): Boolean

    fun hasNotificationPermission(): Boolean
}

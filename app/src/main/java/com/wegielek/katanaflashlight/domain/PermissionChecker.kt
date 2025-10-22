package com.wegielek.katanaflashlight.domain

// PermissionChecker.kt
interface PermissionChecker {
    fun hasCameraPermission(): Boolean

    fun hasNotificationPermission(): Boolean
}

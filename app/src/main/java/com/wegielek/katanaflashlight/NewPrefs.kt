package com.wegielek.katanaflashlight

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object NewPrefs {
    private const val PREFS_NAME = "prefs"

    private val Context.dataStore by preferencesDataStore(PREFS_NAME)

    // Keys
    private val FLASHLIGHT_SERVICE_KEY = booleanPreferencesKey("flashlight_service")
    private val SENSITIVITY_KEY = floatPreferencesKey("sensitivity")
    private val FLASH_KEY = booleanPreferencesKey("flash")
    private val VIBRATION_KEY = booleanPreferencesKey("vibration")
    private val KATANA_KEY = booleanPreferencesKey("katana")
    private val STRENGTH_KEY = intPreferencesKey("strength")
    private val MAX_STRENGTH_KEY = intPreferencesKey("max_strength")
    private val INTRO_KEY = booleanPreferencesKey("intro")

    val Context.isFlashlightServiceStarted: Flow<Boolean>
        get() = dataStore.data.map { it[FLASHLIGHT_SERVICE_KEY] ?: false }

    val Context.sensitivity: Flow<Float>
        get() = dataStore.data.map { it[SENSITIVITY_KEY] ?: 5f }

    val Context.flashOn: Flow<Boolean>
        get() = dataStore.data.map { it[FLASH_KEY] ?: false }

    val Context.vibrationOn: Flow<Boolean>
        get() = dataStore.data.map { it[VIBRATION_KEY] ?: false }

    val Context.katanaOn: Flow<Boolean>
        get() = dataStore.data.map { it[KATANA_KEY] ?: false }

    val Context.strength: Flow<Int>
        get() = dataStore.data.map { it[STRENGTH_KEY] ?: (it[MAX_STRENGTH_KEY] ?: 1) }

    val Context.maximumStrength: Flow<Int>
        get() = dataStore.data.map { it[MAX_STRENGTH_KEY] ?: 1 }

    val Context.introDone: Flow<Boolean>
        get() = dataStore.data.map { it[INTRO_KEY] ?: false }

    suspend fun setFlashlightServiceStarted(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[FLASHLIGHT_SERVICE_KEY] = value }
    }

    suspend fun setSensitivity(
        context: Context,
        value: Float,
    ) {
        context.dataStore.edit { it[SENSITIVITY_KEY] = value }
    }

    suspend fun setFlashOn(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[FLASH_KEY] = value }
    }

    suspend fun setVibrationOn(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[VIBRATION_KEY] = value }
    }

    suspend fun setKatanaOn(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[KATANA_KEY] = value }
    }

    suspend fun setStrength(
        context: Context,
        value: Int,
    ) {
        context.dataStore.edit { it[STRENGTH_KEY] = value }
    }

    suspend fun setMaximumStrength(
        context: Context,
        value: Int,
    ) {
        context.dataStore.edit { it[MAX_STRENGTH_KEY] = value }
    }

    suspend fun setIntroDone(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[INTRO_KEY] = value }
    }
}

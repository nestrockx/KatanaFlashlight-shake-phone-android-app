package com.wegielek.katana_flashlight

import android.content.Context
import android.content.SharedPreferences

class Prefs {
    companion object {
        private fun getPrefs(context: Context, name: String): SharedPreferences {
            return context.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        }

        fun setThreshold(context: Context, value: Float) {
            getPrefs(context, "PREFS").edit().putFloat("threshold", value).apply()
        }

        fun getThreshold(context: Context): Float {
            return getPrefs(context, "PREFS").getFloat("threshold", 10f)
        }

        fun setFlashOn(context: Context, value: Boolean) {
            getPrefs(context, "PREFS").edit().putBoolean("flash", value).apply()
        }

        fun getFlashOn(context: Context): Boolean {
            return getPrefs(context, "PREFS").getBoolean("flash", false)
        }

        fun setVibrationOn(context: Context, value: Boolean) {
            getPrefs(context, "PREFS").edit().putBoolean("vibration", value).apply()
        }

        fun getVibrationOn(context: Context): Boolean {
            return getPrefs(context, "PREFS").getBoolean("vibration", false)
        }

        fun setStrength(context: Context, value: Int) {
            getPrefs(context, "PREFS").edit().putInt("strength", value).apply()
        }

        fun getStrength(context: Context): Int {
            return getPrefs(context, "PREFS").getInt("strength", getMaximumStrength(context))
        }

        fun setMaximumStrength(context: Context, value: Int) {
            getPrefs(context, "PREFS").edit().putInt("max_strength", value).apply()
        }

        private fun getMaximumStrength(context: Context): Int {
            return getPrefs(context, "PREFS").getInt("max_strength", 1)
        }
    }
}
package com.wegielek.katanaflashlight.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.wegielek.katanaflashlight.MainActivity
import com.wegielek.katanaflashlight.NewPrefs
import com.wegielek.katanaflashlight.NewPrefs.flashOn
import com.wegielek.katanaflashlight.NewPrefs.sensitivity
import com.wegielek.katanaflashlight.NewPrefs.strength
import com.wegielek.katanaflashlight.NewPrefs.vibrationOn
import com.wegielek.katanaflashlight.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

class FlashlightForegroundService :
    Service(),
    SensorEventListener {
    private lateinit var wakeLock: PowerManager.WakeLock

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    private fun isCallActive(): Boolean {
        val manager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun acquire(): Runnable =
        Runnable {
            wakeLock.acquire(10 * 60 * 1000L)
            handler.postDelayed(acquire(), 10 * 60 * 1000L)
        }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val powerManager = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                LOG_TAG,
            )
        handler.post(acquire())

        if (intent?.extras?.getInt("close") == 1) {
            Toast.makeText(this, getString(R.string.katana_dismissed), Toast.LENGTH_SHORT).show()
            serviceScope.launch {
                NewPrefs.setKatanaOn(applicationContext, false)
            }
            stopSelf()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraManager = applicationContext.getSystemService(CAMERA_SERVICE) as CameraManager?
            try {
                cameraId = cameraManager?.cameraIdList?.get(0)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, getString(R.string.flashlight_not_available), Toast.LENGTH_SHORT).show()
        }

        accelerometerSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
            )
        }

        startForegroundService()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(motionReset1)
        handler.removeCallbacks(motionReset2)
        handler.removeCallbacks(motionReset3)

        // Unregister sensor listener
        sensorManager.unregisterListener(this)

        // Remove all pending callbacks
        handler.removeCallbacksAndMessages(null)

        // Safely release wake lock
        if (wakeLock.isHeld) {
            wakeLock.release()
        }

        // Ensure torch is off when service stops
        serviceScope.launch {
            try {
                cameraManager?.setTorchMode(cameraId ?: return@launch, false)
                NewPrefs.setFlashOn(applicationContext, false)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to turn off torch in onDestroy", e)
            }
        }

        // Reset katana state
        serviceScope.launch {
            NewPrefs.setKatanaOn(applicationContext, false)
        }

        // Cancel all coroutines to avoid leaks
        serviceScope.cancel()

        Log.i(LOG_TAG, "FlashlightForegroundService destroyed")
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val deleteIntent = Intent(this, FlashlightForegroundService::class.java)
        deleteIntent.putExtra("close", 1)
        val deletePendingIntent =
            PendingIntent.getService(
                this,
                0,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentText(getString(R.string.katana_is_running))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_katana_with_handle)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_katana_with_handle, getString(R.string.close), deletePendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Flashlight Channel",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    setShowBadge(false)
                }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
        private val LOG_TAG: String = FlashlightForegroundService::class.java.getSimpleName()
    }

    private lateinit var sensorManager: SensorManager
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var accelerometerSensor: Sensor? = null

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isCallActive()) {
            if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                val alpha = 0.8f
                val gravity = FloatArray(3)
                val linearAccelerationResult = FloatArray(3)

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                linearAccelerationResult[0] = event.values[0] - gravity[0]
                linearAccelerationResult[1] = event.values[1] - gravity[1]
                linearAccelerationResult[2] = event.values[2] - gravity[2]

                val x = linearAccelerationResult[0]
                val y = linearAccelerationResult[1]
                val z = linearAccelerationResult[2]

                val avg = sqrt(x.pow(2) + y.pow(2))
                Log.i("Sensor data:", "linear acceleration: $x $y $z")

                if (!coolDown) {
                    serviceScope.launch {
                        if (avg >= applicationContext.sensitivity.first() * 3 + 7) {
                            if (motionStep3) {
                                toggleFlashlight()
                                motionStep1 = false
                                motionStep2 = false
                                motionStep3 = false
                                coolDown = true
                                handler.postDelayed({ coolDown = false }, 500)
                            } else if (motionStep2) {
                                handler.postDelayed({ triggerMotionStepThree() }, 150)
                            } else if (motionStep1) {
                                handler.postDelayed({ triggerMotionStepTwo() }, 150)
                            } else {
                                triggerMotionStepOne()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {
    }

    private var coolDown: Boolean = false
    private var motionStep1: Boolean = false
    private var motionStep2: Boolean = false
    private var motionStep3: Boolean = false

    private val motionReset1 = Runnable { motionStep1 = false }
    private val motionReset2 = Runnable { motionStep2 = false }
    private val motionReset3 = Runnable { motionStep3 = false }

    private fun triggerMotionStepOne() {
        motionStep1 = true
        handler.postDelayed(motionReset1, 200)
    }

    private fun triggerMotionStepTwo() {
        motionStep2 = true
        handler.postDelayed(motionReset2, 200)
    }

    private fun triggerMotionStepThree() {
        motionStep3 = true
        handler.postDelayed(motionReset3, 200)
    }

    private fun hasFlashlightStrengthLevels(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val x: Int? =
                cameraManager?.getCameraCharacteristics(cameraId!!)?.get(
                    CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL,
                )
            if (x != null) {
                if (x > 1) {
                    return true
                }
            }
        }
        return false
    }

    private fun toggleFlashlight() {
        serviceScope.launch {
            val flashOn = applicationContext.flashOn.first()
            val vibrationOn = applicationContext.vibrationOn.first()
            val strength = applicationContext.strength.first()
            val hasStrengthLevels = hasFlashlightStrengthLevels()

            try {
                if (flashOn) {
                    // Turn off
                    cameraManager?.setTorchMode(cameraId!!, false)
                    NewPrefs.setFlashOn(applicationContext, false)
                } else {
                    // Turn on
                    if (hasStrengthLevels && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            cameraManager?.turnOnTorchWithStrengthLevel(cameraId!!, strength)
                        } catch (e: IllegalArgumentException) {
                            // fallback if unsupported
                            cameraManager?.setTorchMode(cameraId!!, true)
                            e.printStackTrace()
                        }
                    } else {
                        cameraManager?.setTorchMode(cameraId!!, true)
                    }
                    NewPrefs.setFlashOn(applicationContext, true)
                }

                if (vibrationOn) vibrate()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun vibrate() {
        val v = ContextCompat.getSystemService(applicationContext, Vibrator::class.java) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(300)
        }
    }
}

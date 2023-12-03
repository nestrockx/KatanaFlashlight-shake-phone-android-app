package com.wegielek.katana_flashlight

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
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt


class FlashlightForegroundService : Service(), SensorEventListener {

    private fun isCallActive(context: Context): Boolean {
        val manager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.extras?.getInt("close") == 1) {
            Toast.makeText(this, getString(R.string.katana_dismissed), Toast.LENGTH_SHORT).show()
            stopSelf()
        }

        handler = Handler(Looper.getMainLooper())

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager?
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
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        startForegroundService()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val deleteIntent = Intent(this, FlashlightForegroundService::class.java)
        deleteIntent.putExtra("close", 1)
        val deletePendingIntent = PendingIntent.getService(
            this,
            0,
            deleteIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(getString(R.string.katana_is_running))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_katana_with_handle)
            .setNumber(0)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setDeleteIntent(deletePendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Flashlight Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var handler: Handler
    private var accelerometerSensor: Sensor? = null

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isCallActive(this)) {
            // Check if the sensor type is accelerometer
            if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {

                // Calculate linear acceleration values excluding gravity
                val alpha = 0.8f
                val gravity = FloatArray(3)
                val linearAccelerationResult = FloatArray(3)

                // Apply a low-pass filter to remove gravity contributions
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                // Subtract the gravity to get linear acceleration
                linearAccelerationResult[0] = event.values[0] - gravity[0]
                linearAccelerationResult[1] = event.values[1] - gravity[1]
                linearAccelerationResult[2] = event.values[2] - gravity[2]

                // Use the linear acceleration values
                val x = linearAccelerationResult[0]
                val y = linearAccelerationResult[1]
                val z = linearAccelerationResult[2]

                // Do something with the linear acceleration values (x, y, z)
                // For example, log/print them:
                val avg = sqrt(x.pow(2) + y.pow(2))
                println("acc $x $y $z")

                if (!coolDown) {
                    if (avg >= Prefs.getThreshold(this)) {
                        if (motionStep2) {
                            turnFlashlight()
                            motionStep1 = false
                            motionStep2 = false
                            coolDown = true
                            handler.postDelayed({ coolDown = false }, 700)
                        } else if (motionStep1) {
                            handler.post(motionStepTwo())
                        } else {
                            handler.post(motionStepOne())
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private var coolDown: Boolean = false
    private var motionStep1: Boolean = false
    private var motionStep2: Boolean = false

    private fun motionStepOne(): Runnable {
        motionStep1 = true
        return Runnable {
            handler.postDelayed({ motionStep1 = false }, 300)
        }
    }

    private fun motionStepTwo(): Runnable {
        motionStep2 = true
        return Runnable {
            handler.postDelayed({ motionStep2 = false }, 300)
        }
    }

    private fun hasFlashlightStrengthLevels(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val x: Int? = cameraManager?.getCameraCharacteristics(cameraId!!)?.get(
                CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL
            )
            if (x != null) {
                if (x > 1) {
                    return true
                }
            }
        }
        return false
    }

    private fun getFlashlightMaximumStrengthLevel(): Int? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraManager?.getCameraCharacteristics(cameraId!!)?.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL)
        } else {
            1
        }
    }

    private fun turnFlashlight() {
        if (!Prefs.getFlashOn(this)) {
            try {
                if (hasFlashlightStrengthLevels()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        cameraManager?.turnOnTorchWithStrengthLevel(cameraId!!, Prefs.getStrength(this))
                    }
                } else {
                    cameraManager?.setTorchMode(cameraId!!, true)
                }
                Prefs.setFlashOn(this, !Prefs.getFlashOn(this))
                if (Prefs.getVibrationOn(this)) {
                    vibrate()
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            try {
                cameraManager?.setTorchMode(cameraId!!, false)
                Prefs.setFlashOn(this, !Prefs.getFlashOn(this))
                if (Prefs.getVibrationOn(this)) {
                    vibrate()
                }
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
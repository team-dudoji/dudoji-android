package com.dudoji.android.map.utils


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class MapDirectionController(
    context: Context,
    private val mapCameraController: MapCameraPositionController,
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun start() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        val orientationAngles = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        val bearing = (azimuth + 360) % 360

        Log.d("MapDirection", "${bearing}")

        mapCameraController.setBearing(bearing)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

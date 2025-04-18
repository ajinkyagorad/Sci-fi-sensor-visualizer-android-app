package com.example.aigenapp.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaRecorder
import android.util.Log
import com.example.aigenapp.data.SensorData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class SensorDataManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val _sensorData = MutableStateFlow(SensorData())
    val sensorData: StateFlow<SensorData> = _sensorData

    private var mediaRecorder: MediaRecorder? = null
    
    private val maxHistorySize = 100
    private val accelerometerHistory = mutableListOf<List<Float>>()
    private val gyroscopeHistory = mutableListOf<List<Float>>()
    private val magnetometerHistory = mutableListOf<List<Float>>()
    private val audioHistory = mutableListOf<List<Float>>()
    
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val currentAudioLevel = mediaRecorder?.maxAmplitude?.toFloat() ?: 0f
            
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    updateSensorHistory(accelerometerHistory, event.values.toList())
                    _sensorData.value = _sensorData.value.copy(
                        accelerometer = Triple(event.values[0], event.values[1], event.values[2]),
                        accelerometerHistory = accelerometerHistory.toList()
                    )
                }
                Sensor.TYPE_GYROSCOPE -> {
                    updateSensorHistory(gyroscopeHistory, event.values.toList())
                    _sensorData.value = _sensorData.value.copy(
                        gyroscope = Triple(event.values[0], event.values[1], event.values[2]),
                        gyroscopeHistory = gyroscopeHistory.toList()
                    )
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    updateSensorHistory(magnetometerHistory, event.values.toList())
                    _sensorData.value = _sensorData.value.copy(
                        magnetometer = Triple(event.values[0], event.values[1], event.values[2]),
                        magnetometerHistory = magnetometerHistory.toList()
                    )
                }
            }
            
            // Update audio level and history
            updateSensorHistory(audioHistory, listOf(currentAudioLevel))
            _sensorData.value = _sensorData.value.copy(
                audioLevel = currentAudioLevel,
                audioHistory = audioHistory.toList()
            )
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun startSensors() {
        try {
            Log.d("SensorDebug", "Starting sensor initialization...")
            
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            Log.d("SensorDebug", "Accelerometer available: ${accelerometer != null}")
            
            val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            Log.d("SensorDebug", "Gyroscope available: ${gyroscope != null}")
            
            val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            Log.d("SensorDebug", "Magnetometer available: ${magnetometer != null}")

            if (accelerometer == null && gyroscope == null && magnetometer == null) {
                throw Exception("No sensors available on this device")
            }

            accelerometer?.let {
                sensorManager.registerListener(
                    sensorListener,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }

            gyroscope?.let {
                sensorManager.registerListener(
                    sensorListener,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }

            magnetometer?.let {
                sensorManager.registerListener(
                    sensorListener,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }

            startAudioRecording()
        } catch (e: Exception) {
            throw Exception("Failed to start sensors: ${e.message}")
        }
    }

    private fun startAudioRecording() {
        try {
            Log.d("SensorDebug", "Starting audio recording initialization...")
            
            // Create a temporary file in the app's cache directory
            val outputFile = File(context.cacheDir, "temp_audio.3gp")
            Log.d("SensorDebug", "Audio output file path: ${outputFile.absolutePath}")
            
            mediaRecorder = MediaRecorder().apply {
                Log.d("SensorDebug", "Setting up MediaRecorder...")
                setAudioSource(MediaRecorder.AudioSource.MIC)
                Log.d("SensorDebug", "Audio source set")
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                Log.d("SensorDebug", "Output format set")
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                Log.d("SensorDebug", "Audio encoder set")
                setOutputFile(outputFile.absolutePath)
                Log.d("SensorDebug", "Output file set")
                prepare()
                Log.d("SensorDebug", "MediaRecorder prepared")
                start()
                Log.d("SensorDebug", "MediaRecorder started")
            }
        } catch (e: Exception) {
            Log.e("SensorDebug", "Audio recording failed: ${e.message}", e)
            throw Exception("Failed to initialize audio recording: ${e.message}")
        }
    }

    private fun updateSensorHistory(history: MutableList<List<Float>>, values: List<Float>) {
        history.add(values)
        if (history.size > maxHistorySize) {
            history.removeAt(0)
        }
    }

    fun stopSensors() {
        try {
            sensorManager.unregisterListener(sensorListener)
            mediaRecorder?.apply {
                try {
                    stop()
                    release()
                } catch (e: Exception) {
                    // Ignore errors when stopping recording
                }
            }
            mediaRecorder = null
        } catch (e: Exception) {
            // Log error but don't throw as this is cleanup code
            e.printStackTrace()
        }
    }


}

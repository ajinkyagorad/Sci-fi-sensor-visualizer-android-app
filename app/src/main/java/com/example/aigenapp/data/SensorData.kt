package com.example.aigenapp.data

data class SensorData(
    val accelerometer: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val gyroscope: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val magnetometer: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val audioLevel: Float = 0f,
    val accelerometerHistory: List<List<Float>> = emptyList(),
    val gyroscopeHistory: List<List<Float>> = emptyList(),
    val magnetometerHistory: List<List<Float>> = emptyList(),
    val audioHistory: List<List<Float>> = emptyList()
)

data class WeatherData(
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val pressure: Float = 0f,
    val description: String = ""
)

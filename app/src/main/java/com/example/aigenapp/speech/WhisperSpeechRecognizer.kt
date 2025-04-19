package com.example.aigenapp.speech

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WhisperSpeechRecognizer(private val context: Context, private val apiKey: String) {
    private val _transcribedText = MutableStateFlow("")
    val transcribedText: StateFlow<String> = _transcribedText.asStateFlow()
    fun setTranscribedText(newText: String) {
        _transcribedText.value = newText
    }

    private var recorder: MediaRecorder? = null
    private var audioFile: File? = null

    fun startRecording() {
        audioFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "whisper_input.mp4")
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(audioFile?.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording(): File? {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        return audioFile
    }

    suspend fun transcribeAudio(): String? = withContext(Dispatchers.IO) {
        try {
            val file = audioFile ?: throw IOException("No audio file available")
            if (!file.exists()) throw IOException("Audio file does not exist")
            
            val client = OkHttpClient.Builder()
                .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build()
                
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody("audio/mp4".toMediaTypeOrNull()))
                .addFormDataPart("model", "whisper-1")
                .build()
                
            val request = Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .header("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()
                
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (!response.isSuccessful || responseBody == null) {
                throw IOException("API call failed: ${response.code} - ${responseBody ?: "No response body"}")
            }
            
            val json = JSONObject(responseBody)
            val text = json.optString("text")
            if (text.isBlank()) throw IOException("No transcription in response")
            
            return@withContext text
        } catch (e: Exception) {
            android.util.Log.e("WhisperRecognizer", "Error transcribing: ${e.message}")
            throw e
        }
    }
}

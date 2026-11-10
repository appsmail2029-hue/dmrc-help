package com.example.metro.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class AlarmSoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var alarmJob: Job? = null
    var isSoundEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun startAlarm(isHighPriority: Boolean = false) {
        if (!isSoundEnabled) return
        stopAlarm()

        // Elevate alarm stream volume to maximum
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                am.setStreamVolume(AudioManager.STREAM_ALARM, maxVol, 0)
            }
        } catch (e: Exception) {
            // Ignore volume adjustment restrictions gracefully
        }

        alarmJob = scope.launch {
            try {
                val sampleRate = 44100
                // High priority: piercing 960Hz & 1440Hz alternation; Normal/Broadcast: 880Hz & 1174Hz
                val lowFreq = if (isHighPriority) 960.0 else 880.0
                val highFreq = if (isHighPriority) 1440.0 else 1174.66
                val toneDurationMs = if (isHighPriority) 160 else 240

                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(bufferSize * 2)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    AudioTrack(
                        AudioManager.STREAM_ALARM,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize * 2,
                        AudioTrack.MODE_STREAM
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    audioTrack.setVolume(1.0f)
                }
                audioTrack.play()

                // Trigger vibration
                triggerVibration(isHighPriority)

                var toggle = false
                while (isActive) {
                    val freq = if (toggle) highFreq else lowFreq
                    toggle = !toggle

                    val numSamples = (sampleRate * (toneDurationMs / 1000.0)).toInt()
                    val generatedSnd = ShortArray(numSamples)
                    for (i in 0 until numSamples) {
                        val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                        // Maximized sine wave amplitude (0.98 of Short.MAX_VALUE) for high volume alarm
                        val sample = (sin(angle) * Short.MAX_VALUE * 0.98).toInt().toShort()
                        generatedSnd[i] = sample
                    }

                    audioTrack.write(generatedSnd, 0, generatedSnd.size)
                    delay(toneDurationMs.toLong() + 20L)
                }

                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // AudioTrack or vibration interrupted safely
            }
        }
    }

    fun playSingleChime() {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()
                val freqs = listOf(587.33, 880.0, 1174.66) // D5, A5, D6 joyful metro chime
                for (f in freqs) {
                    val numSamples = (sampleRate * 0.12).toInt()
                    val pcm = ShortArray(numSamples)
                    for (i in 0 until numSamples) {
                        val decay = 1.0 - (i.toDouble() / numSamples)
                        val sample = (sin(2.0 * Math.PI * i / (sampleRate / f)) * Short.MAX_VALUE * 0.6 * decay).toInt().toShort()
                        pcm[i] = sample
                    }
                    track.write(pcm, 0, pcm.size)
                }
                delay(400)
                track.stop()
                track.release()
            } catch (e: Exception) {
                // safely ignored
            }
        }
    }

    fun stopAlarm() {
        alarmJob?.cancel()
        alarmJob = null
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            // ignore
        }
    }

    fun isAlarmActive(): Boolean = alarmJob?.isActive == true

    private fun triggerVibration(isHighPriority: Boolean) {
        try {
            vibrator?.let { vib ->
                if (vib.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val timings = if (isHighPriority) {
                            longArrayOf(0, 300, 100, 300, 100, 300)
                        } else {
                            longArrayOf(0, 400, 200, 400)
                        }
                        val amplitudes = if (isHighPriority) {
                            intArrayOf(0, 255, 0, 255, 0, 255)
                        } else {
                            intArrayOf(0, 200, 0, 200)
                        }
                        vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vib.vibrate(if (isHighPriority) 800 else 500)
                    }
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}

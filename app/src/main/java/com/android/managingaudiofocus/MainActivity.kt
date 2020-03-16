package com.android.managingaudiofocus

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var audioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    val message = "AUDIOFOCUS_GAIN"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    Log.i(TAG, message)
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    val message = "AUDIOFOCUS_LOSS"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    Log.i(TAG, message)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    val message = "AUDIOFOCUS_LOSS_TRANSIENT"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    Log.i(TAG, message)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    val message = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    Log.i(TAG, message)
                }
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> {
                    val message = "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    Log.i(TAG, message)
                }
            }
        }

    private var audioFocusRequest: AudioFocusRequest? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_GAME)
                    setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(audioFocusChangeListener)
                build()
            }
        } else {
            null
        }

    private var audioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        findViewById<Button>(R.id.request_audio_focus_button).setOnClickListener {
            val message = "Requested audio focus: ${requestAudioFocus()}"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            Log.i(TAG, message)
        }
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        } else {
            audioManager?.abandonAudioFocus(null)
        }
        super.onStop()
    }

    private fun requestAudioFocus(): Boolean {
        var res: Int?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { res = audioManager?.requestAudioFocus(it) }
        }
        res = audioManager?.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return res == AudioManager.AUDIOFOCUS_GAIN
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}

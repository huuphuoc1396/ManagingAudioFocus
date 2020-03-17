package com.android.managingaudiofocus

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var audioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    val message = "AUDIOFOCUS_GAIN"
                    toast(message)
                    Log.i(TAG, message)
                    isPlaying = true
                    updatePlayPause()
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    val message = "AUDIOFOCUS_LOSS"
                    toast(message)
                    Log.i(TAG, message)
                    isPlaying = false
                    updatePlayPause()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    val message = "AUDIOFOCUS_LOSS_TRANSIENT"
                    toast(message)
                    Log.i(TAG, message)
                    isPlaying = false
                    updatePlayPause()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    val message = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"
                    toast(message)
                    Log.i(TAG, message)
                    isPlaying = false
                    updatePlayPause()
                }
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> {
                    val message = "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE"
                    toast(message)
                    Log.i(TAG, message)
                    isPlaying = true
                    updatePlayPause()
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


    private var playPauseButton: ImageButton? = null
    private var audioManager: AudioManager? = null
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        playPauseButton = findViewById<ImageButton>(R.id.request_audio_focus_button).apply {
            updatePlayPause()
            setOnClickListener {
                isPlaying = isPlaying.not()
                if (isPlaying) {
                    val message: String
                    if (requestAudioFocus()) {
                        message = "Requested audio focus: Success"
                    } else {
                        isPlaying = false
                        message = "Requested audio focus: Fail"
                    }
                    toast(message)
                    Log.i(TAG, message)
                }
                updatePlayPause()
            }
        }
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        } else {
            audioManager?.abandonAudioFocus(null)
        }
        isPlaying = false
        updatePlayPause()
        super.onStop()
    }

    private fun updatePlayPause() {
        if (isPlaying) {
            // TODO: Play your player
            playPauseButton?.setImageDrawable(getDrawable(R.drawable.ic_pause))
        } else {
            // TODO: Pause your player
            playPauseButton?.setImageDrawable(getDrawable(R.drawable.ic_play))
        }
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

    private fun toast(message: String){
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity_DEBUG"
    }
}

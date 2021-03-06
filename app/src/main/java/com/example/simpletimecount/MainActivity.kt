package com.example.simpletimecount

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import com.example.simpletimecount.databinding.ActivityMainBinding

const val DELAY_INTERVAL = 1000L

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val handler by lazy {
        binding.root.handler
    }

    private val updateUIRunnable = object : Runnable {
        override fun run() {
            updateTimeCount()
            handler.postDelayed(this, DELAY_INTERVAL)
        }
    }

    private var startTimeStamp = 0L // it's like a pauseOffset

    private var state = TimeState.STOP // initialize with STOP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initListeners()

    }

    private fun initListeners() = with(binding) {
        startBtn.setOnClickListener {
            if (state == TimeState.STOP) {
                startTimeCount()
                state = TimeState.START
                Toast.makeText(this@MainActivity, "START", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (state == TimeState.START || state == TimeState.RESUME) {
                Toast.makeText(this@MainActivity, "it's already started.", Toast.LENGTH_SHORT).show()
            }
        }

        pauseBtn.setOnClickListener {
            if (state == TimeState.START || state == TimeState.RESUME) {
                pauseTimeCount()
                state = TimeState.PAUSE
                Toast.makeText(this@MainActivity, "PAUSE", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (state == TimeState.PAUSE) {
                Toast.makeText(this@MainActivity, "it's already paused.", Toast.LENGTH_SHORT).show()
            }
        }

        resumeBtn.setOnClickListener {
            if (state == TimeState.PAUSE) {
                resumeTimeCount()
                state = TimeState.RESUME
                Toast.makeText(this@MainActivity, "RESUME", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (state == TimeState.RESUME) {
                Toast.makeText(this@MainActivity, "it's already resumed.", Toast.LENGTH_SHORT).show()
            }
        }

        stopBtn.setOnClickListener {
            if (state == TimeState.START || state == TimeState.PAUSE || state == TimeState.RESUME) {
                stopTimeCount()
                state = TimeState.STOP
                Toast.makeText(this@MainActivity, "STOP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (state == TimeState.STOP) {
                Toast.makeText(this@MainActivity, "it's already stopped.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startTimeCount() {
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(updateUIRunnable)
    }

    private fun resumeTimeCount() {
        startTimeStamp = SystemClock.elapsedRealtime() - startTimeStamp
        handler?.post(updateUIRunnable)
    }

    private fun pauseTimeCount() {
        startTimeStamp = SystemClock.elapsedRealtime() - startTimeStamp
        handler?.removeCallbacks(updateUIRunnable)
    }

    @SuppressLint("SetTextI18n")
    private fun stopTimeCount() {
        binding.timeTextView.text = "00:00"
        startTimeStamp = 0L
        handler?.removeCallbacks(updateUIRunnable)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeCount() {
        val currentTimeStamp = SystemClock.elapsedRealtime()
        val countTimeSeconds = ((currentTimeStamp - startTimeStamp) / 1000L).toInt()
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60
        binding.timeTextView.text = "%02d:%02d".format(minutes, seconds)
    }

    enum class TimeState {
        START,
        PAUSE,
        RESUME,
        STOP
    }

}

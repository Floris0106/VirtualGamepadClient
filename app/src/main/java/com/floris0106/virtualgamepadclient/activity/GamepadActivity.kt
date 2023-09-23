package com.floris0106.virtualgamepadclient.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.floris0106.virtualgamepadclient.R
import com.floris0106.virtualgamepadclient.net.GamepadStatePacket
import com.floris0106.virtualgamepadclient.net.ServerConnection
import com.floris0106.virtualgamepadclient.view.GamepadView
import java.util.function.Consumer

class GamepadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamepad)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE

        window.insetsController?.let {
            it.hide(WindowInsets.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val connection = ServerConnection.get()
        if (connection == null) {
            exit("No connection")
            return
        }

        findViewById<GamepadView>(R.id.gamepadView).stateChangeListener = Consumer { state ->
            connection.send(GamepadStatePacket(state))
        }

        connection.timeoutListener = Runnable {
            exit("Connection timed out")
        }
    }

    private fun exit(message: String? = null) {
        MainActivity.message = message ?: ""
        finish()
    }
}
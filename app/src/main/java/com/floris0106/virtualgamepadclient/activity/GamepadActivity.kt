package com.floris0106.virtualgamepadclient.activity

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.floris0106.virtualgamepadclient.R
import com.floris0106.virtualgamepadclient.net.GamepadStatePacket
import com.floris0106.virtualgamepadclient.net.ServerConnection
import com.floris0106.virtualgamepadclient.view.GamepadView
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

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

		val gamepadView = findViewById<GamepadView>(R.id.gamepadView)
		executor?.shutdownNow()
		executor = Executors.newSingleThreadScheduledExecutor()
		executor?.scheduleAtFixedRate({
			connection.send(GamepadStatePacket(gamepadView.state))
		}, 0, 1, TimeUnit.MILLISECONDS)

		connection.timeoutListener = Runnable {
			exit("Connection timed out")
		}
	}

	override fun onBackPressed() {
		AlertDialog.Builder(this)
			.setMessage("Are you sure you want to exit?")
			.setPositiveButton("Yes") { _, _ ->
				exit()
			}
			.setNegativeButton("No") { _, _ -> }
			.create().show()
	}

	private fun exit(message: String? = null) {
		executor?.shutdownNow()
		ServerConnection.get()?.close()
		MainActivity.message = message ?: ""
		finish()
	}

	companion object {
		private var executor: ScheduledExecutorService? = null
	}
}
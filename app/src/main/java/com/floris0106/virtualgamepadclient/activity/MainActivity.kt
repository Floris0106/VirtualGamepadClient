package com.floris0106.virtualgamepadclient.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.floris0106.virtualgamepadclient.R
import com.floris0106.virtualgamepadclient.net.ServerConnection
import com.floris0106.virtualgamepadclient.view.GamepadView
import java.net.Inet4Address
import java.net.InetAddress
import java.time.Instant

class MainActivity : AppCompatActivity() {
	private lateinit var ipEditText: EditText
	private lateinit var connectButton: Button
	private lateinit var editLayoutButton: Button
	private lateinit var resetLayoutButton: Button
	private lateinit var progressBar: ProgressBar
	private lateinit var messageTextView: TextView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		ipEditText = findViewById(R.id.ipEditText)
		connectButton = findViewById(R.id.connectButton)
		editLayoutButton = findViewById(R.id.editLayoutButton)
		resetLayoutButton = findViewById(R.id.resetLayoutButton)
		progressBar = findViewById(R.id.progressBar)
		messageTextView = findViewById(R.id.messageTextView)

		connectButton.setOnClickListener {
			val ip: InetAddress
			try {
				ip = Inet4Address.getByName(ipEditText.text.toString())
			}
			catch (e: Exception) {
				setMessage("Invalid IP")
				return@setOnClickListener
			}

			setBusy(true)

			Thread {
				val connection = ServerConnection.create(ip)

				val endTime = Instant.now().plusSeconds(5)
				while (Instant.now().isBefore(endTime)) {
					if (connection.connected)
						break
					Thread.sleep(100)
				}

				if (!connection.connected)
					connection.close()

				Handler(mainLooper).post {
					setBusy(false)
					if (!connection.connected) {
						setMessage("Failed to connect")
						connection.close()
						return@post
					}
					startActivity(Intent(this, GamepadActivity::class.java))
				}
			}.start()
		}

		editLayoutButton.setOnClickListener {
			startActivity(Intent(this, LayoutActivity::class.java))
		}

		resetLayoutButton.setOnClickListener {
			AlertDialog.Builder(this)
				.setMessage("Are you sure you want to reset the layout?")
				.setPositiveButton("No") { _, _ -> }
				.setNegativeButton("Yes (default)") { _, _ ->
					GamepadView.layout = GamepadView.Layout()
				}
				.setNeutralButton("Yes (alternative)") { _, _ ->
					GamepadView.layout = GamepadView.Layout.getAlternativeLayout()
				}
				.create().show()
		}

		setMessage(message)

		GamepadView.loadLayout(this)
	}

	private fun setBusy(busy: Boolean) {
		ipEditText.isEnabled = !busy
		connectButton.isEnabled = !busy
		editLayoutButton.isEnabled = !busy
		resetLayoutButton.isEnabled = !busy
		progressBar.visibility = if (busy) ProgressBar.VISIBLE else ProgressBar.INVISIBLE
		if (busy)
			messageTextView.text = ""
	}

	private fun setMessage(message: String) {
		messageTextView.text = message
	}

	companion object {
		var message = ""
	}
}
package com.floris0106.virtualgamepadclient.activity

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.floris0106.virtualgamepadclient.R
import com.floris0106.virtualgamepadclient.view.GamepadView

class LayoutActivity : AppCompatActivity() {
	private val oldLayout = GamepadView.layout.clone()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_layout)

		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE

		window.insetsController?.let {
			it.hide(WindowInsets.Type.systemBars())
			it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
		}

		findViewById<GamepadView>(R.id.gamepadView).editMode = true
	}

	override fun onBackPressed() {
		AlertDialog.Builder(this)
			.setMessage("Do you want to save the layout?")
			.setPositiveButton("Yes") { _, _ ->
				GamepadView.saveLayout(this)
				finish()
			}
			.setNegativeButton("No") { _, _ ->
				GamepadView.layout = oldLayout
				finish()
			}
			.setNeutralButton("Cancel") { _, _ -> }
			.create().show()
	}
}
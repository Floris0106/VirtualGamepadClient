package com.floris0106.virtualgamepadclient.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.div
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import com.floris0106.virtualgamepadclient.R
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min

class GamepadView : View {
	private val bitmapA = BitmapFactory.decodeResource(resources, R.drawable.a)
	private val bitmapAPressed = BitmapFactory.decodeResource(resources, R.drawable.a_pressed)
	private val bitmapB = BitmapFactory.decodeResource(resources, R.drawable.b)
	private val bitmapBPressed = BitmapFactory.decodeResource(resources, R.drawable.b_pressed)
	private val bitmapX = BitmapFactory.decodeResource(resources, R.drawable.x)
	private val bitmapXPressed = BitmapFactory.decodeResource(resources, R.drawable.x_pressed)
	private val bitmapY = BitmapFactory.decodeResource(resources, R.drawable.y)
	private val bitmapYPressed = BitmapFactory.decodeResource(resources, R.drawable.y_pressed)
	private val bitmapDPad = BitmapFactory.decodeResource(resources, R.drawable.dpad)
	private val bitmapDPadUp = BitmapFactory.decodeResource(resources, R.drawable.dpad_up)
	private val bitmapDPadDown = BitmapFactory.decodeResource(resources, R.drawable.dpad_down)
	private val bitmapDPadLeft = BitmapFactory.decodeResource(resources, R.drawable.dpad_left)
	private val bitmapDPadRight = BitmapFactory.decodeResource(resources, R.drawable.dpad_right)
	private val bitmapStickSocket = BitmapFactory.decodeResource(resources, R.drawable.stick_socket)
	private val bitmapLeftStick = BitmapFactory.decodeResource(resources, R.drawable.left_stick)
	private val bitmapRightStick = BitmapFactory.decodeResource(resources, R.drawable.right_stick)
	private val bitmapL = BitmapFactory.decodeResource(resources, R.drawable.l)
	private val bitmapLPressed = BitmapFactory.decodeResource(resources, R.drawable.l_pressed)
	private val bitmapZL = BitmapFactory.decodeResource(resources, R.drawable.zl)
	private val bitmapZLPressed = BitmapFactory.decodeResource(resources, R.drawable.zl_pressed)
	private val bitmapR = BitmapFactory.decodeResource(resources, R.drawable.r)
	private val bitmapRPressed = BitmapFactory.decodeResource(resources, R.drawable.r_pressed)
	private val bitmapZR = BitmapFactory.decodeResource(resources, R.drawable.zr)
	private val bitmapZRPressed = BitmapFactory.decodeResource(resources, R.drawable.zr_pressed)
	private val bitmapMinus = BitmapFactory.decodeResource(resources, R.drawable.minus)
	private val bitmapMinusPressed = BitmapFactory.decodeResource(resources, R.drawable.minus_pressed)
	private val bitmapPlus = BitmapFactory.decodeResource(resources, R.drawable.plus)
	private val bitmapPlusPressed = BitmapFactory.decodeResource(resources, R.drawable.plus_pressed)

	private val buttons = listOf(
		Button(layout.transformA) { state.a = it },
		Button(layout.transformB) { state.b = it },
		Button(layout.transformX) { state.x = it },
		Button(layout.transformY) { state.y = it },
		Button(layout.transformL) { state.l = it },
		Button(layout.transformZL) { state.zl = it },
		Button(layout.transformR) { state.r = it },
		Button(layout.transformZR) { state.zr = it },
		Button(layout.transformMinus) { state.minus = it },
		Button(layout.transformPlus) { state.plus = it },
	)

	private val joysticks = listOf(
		Joystick(layout.transformLeftStickSocket, 50f, { vec ->
			layout.transformLeftStickCap.position = vec + layout.transformLeftStickSocket.position
			state.leftStick = vec / layout.transformLeftStickSocket.size
		}),
		Joystick(layout.transformRightStickSocket, 50f, { vec ->
			layout.transformRightStickCap.position = vec + layout.transformRightStickSocket.position
			state.rightStick = vec / layout.transformRightStickSocket.size
		})
	)

	private val pointers = mutableMapOf<Int, Pointer>()

	private val transformEditors = mutableMapOf<Transform, Pointer?>(
		layout.transformA to null,
		layout.transformB to null,
		layout.transformX to null,
		layout.transformY to null,
		layout.transformDPad to null,
		layout.transformLeftStickSocket to null,
		layout.transformRightStickSocket to null,
		layout.transformL to null,
		layout.transformZL to null,
		layout.transformR to null,
		layout.transformZR to null,
		layout.transformMinus to null,
		layout.transformPlus to null
	)

	private var scale = 1f

	val state = GamepadState()

	var editMode = false

	constructor(context: Context) : super(context)
	constructor(context: Context, attributes: AttributeSet) : super(context, attributes)
	constructor(context: Context, attributes: AttributeSet, defaultStyle: Int) : super(context, attributes, defaultStyle)

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		scale = min(width / 2312f, height / 1080f)
		canvas.scale(scale, scale, width * 0.5f, height * 0.5f)

		drawBitmap(canvas, if (state.a) bitmapAPressed else bitmapA, layout.transformA)
		drawBitmap(canvas, if (state.b) bitmapBPressed else bitmapB, layout.transformB)
		drawBitmap(canvas, if (state.x) bitmapXPressed else bitmapX, layout.transformX)
		drawBitmap(canvas, if (state.y) bitmapYPressed else bitmapY, layout.transformY)
		drawBitmap(canvas, bitmapDPad, layout.transformDPad)
		if (state.dpadUp)
			drawBitmap(canvas, bitmapDPadUp, layout.transformDPad)
		if (state.dpadDown)
			drawBitmap(canvas, bitmapDPadDown, layout.transformDPad)
		if (state.dpadLeft)
			drawBitmap(canvas, bitmapDPadLeft, layout.transformDPad)
		if (state.dpadRight)
			drawBitmap(canvas, bitmapDPadRight, layout.transformDPad)
		drawBitmap(canvas, bitmapStickSocket, layout.transformLeftStickSocket)
		drawBitmap(canvas, bitmapLeftStick, layout.transformLeftStickCap)
		drawBitmap(canvas, bitmapStickSocket, layout.transformRightStickSocket)
		drawBitmap(canvas, bitmapRightStick, layout.transformRightStickCap)
		drawBitmap(canvas, if (state.l) bitmapLPressed else bitmapL, layout.transformL)
		drawBitmap(canvas, if (state.zl) bitmapZLPressed else bitmapZL, layout.transformZL)
		drawBitmap(canvas, if (state.r) bitmapRPressed else bitmapR, layout.transformR)
		drawBitmap(canvas, if (state.zr) bitmapZRPressed else bitmapZR, layout.transformZR)
		drawBitmap(canvas, if (state.minus) bitmapMinusPressed else bitmapMinus, layout.transformMinus)
		drawBitmap(canvas, if (state.plus) bitmapPlusPressed else bitmapPlus, layout.transformPlus)
	}

	override fun onTouchEvent(event: MotionEvent?): Boolean {
		if (event == null)
			return false

		when (event.actionMasked) {
			MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
				val index = event.actionIndex
				val id = event.getPointerId(index)
				val position = fromScreenSpace(PointF(event.getX(index), event.getY(index)))

				if (editMode) {
					transformEditors.keys.firstOrNull { transform ->
						transformEditors[transform] == null && position.isWithin(transform.getRect())
					}.let { transform ->
						pointers[id] = Pointer(position, transform)
						if (transform != null)
							transformEditors[transform] = pointers[id]
					}

					checkEditState()
					return true
				}

				joysticks.firstOrNull { joystick ->
					joystick.controllingPointer == null && position.isWithin(joystick.transform.getRect())
				}.let { joystick ->
					pointers[id] = Pointer(position, joystick)
					joystick?.controllingPointer = pointers[id]
				}

				checkState()
				return true
			}
			MotionEvent.ACTION_MOVE -> {
				for (i in 0 until event.pointerCount) {
					val id = event.getPointerId(i)
					val position = fromScreenSpace(PointF(event.getX(i), event.getY(i)))
					val pointer = pointers[id]!!
					pointer.delta = position - pointer.position
					pointer.position = position
				}

				if (editMode)
					checkEditState()
				else
					checkState()

				return true
			}
			MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
				pointers.remove(event.getPointerId(event.actionIndex))?.controlledJoystick?.controllingPointer = null

				if (editMode) {
					checkEditState()
					return true
				}

				checkState()
				return true
			}
		}

		return super.onTouchEvent(event)
	}

	private fun checkState() {
		state.a = false
		state.b = false
		state.x = false
		state.y = false
		state.dpadUp = false
		state.dpadDown = false
		state.dpadLeft = false
		state.dpadRight = false
		state.l = false
		state.zl = false
		state.r = false
		state.zr = false
		state.minus = false
		state.plus = false

		for (button in buttons)
			button.stateSetter.accept(pointers.values.any { it.controlledJoystick == null && it.position.isWithin(button.transform.getRect()) })

		for (joystick in joysticks) {
			if (joystick.controllingPointer == null)
				joystick.directionSetter.accept(PointF(0f, 0f))
			else {
				val pointer = joystick.controllingPointer!!
				val direction = pointer.position - joystick.transform.position
				val clamped = direction / max(direction.length() / joystick.radius, 1f)
				joystick.directionSetter.accept(clamped)
			}
		}

		for (pointer in pointers.values.filter { it.controlledJoystick == null && it.position.isWithin(layout.transformDPad.getRect()) }) {
			val position = (pointer.position - layout.transformDPad.position) / layout.transformDPad.size

			if (position.x < -0.2f)
				state.dpadLeft = true
			else if (position.x > 0.2f)
				state.dpadRight = true

			if (position.y < -0.2f)
				state.dpadDown = true
			else if (position.y > 0.2f)
				state.dpadUp = true
		}

		if (state.dpadLeft && state.dpadRight) {
			state.dpadLeft = false
			state.dpadRight = false
		}
		if (state.dpadUp && state.dpadDown) {
			state.dpadUp = false
			state.dpadDown = false
		}

		invalidate()
	}

	private fun checkEditState() {
		for (pointer in pointers.values) {
			if (pointer.controlledTransform != null) {
				pointer.controlledTransform.position += pointer.delta
				if (pointer.controlledTransform.child != null)
					pointer.controlledTransform.child!!.position = pointer.controlledTransform.position
			}
		}

		invalidate()
	}

	private fun drawBitmap(canvas: Canvas, bitmap: Bitmap, transform: Transform) {
		canvas.drawBitmap(bitmap, null, toScreenSpace(transform.getRect()), null)
	}

	private fun fromScreenSpace(point: PointF): PointF {
		return PointF(point.x - width * 0.5f, -point.y + height * 0.5f) / scale
	}

	private fun toScreenSpace(rectF: RectF): RectF {
		return RectF(rectF.left + width * 0.5f, -rectF.top + height * 0.5f, rectF.right + width * 0.5f, -rectF.bottom + height * 0.5f)
	}

	private fun PointF.isWithin(rect: RectF): Boolean {
		return rect.left <= x && x <= rect.right && rect.bottom <= y && y <= rect.top
	}

	companion object {
		private const val PREFERENCE_FILE_KEY = "gamepad_layout"
		private const val TRANSFORM_A_X_KEY = "transform_a_x"
		private const val TRANSFORM_A_Y_KEY = "transform_a_y"
		private const val TRANSFORM_B_X_KEY = "transform_b_x"
		private const val TRANSFORM_B_Y_KEY = "transform_b_y"
		private const val TRANSFORM_X_X_KEY = "transform_x_x"
		private const val TRANSFORM_X_Y_KEY = "transform_x_y"
		private const val TRANSFORM_Y_X_KEY = "transform_y_x"
		private const val TRANSFORM_Y_Y_KEY = "transform_y_y"
		private const val TRANSFORM_DPAD_X_KEY = "transform_dpad_x"
		private const val TRANSFORM_DPAD_Y_KEY = "transform_dpad_y"
		private const val TRANSFORM_LEFT_STICK_X_KEY = "transform_stick_l_x"
		private const val TRANSFORM_LEFT_STICK_Y_KEY = "transform_stick_l_y"
		private const val TRANSFORM_RIGHT_STICK_X_KEY = "transform_stick_r_x"
		private const val TRANSFORM_RIGHT_STICK_Y_KEY = "transform_stick_r_y"
		private const val TRANSFORM_L_X_KEY = "transform_l_x"
		private const val TRANSFORM_L_Y_KEY = "transform_l_y"
		private const val TRANSFORM_ZL_X_KEY = "transform_zl_x"
		private const val TRANSFORM_ZL_Y_KEY = "transform_zl_y"
		private const val TRANSFORM_R_X_KEY = "transform_r_x"
		private const val TRANSFORM_R_Y_KEY = "transform_r_y"
		private const val TRANSFORM_ZR_X_KEY = "transform_zr_x"
		private const val TRANSFORM_ZR_Y_KEY = "transform_zr_y"
		private const val TRANSFORM_MINUS_X_KEY = "transform_minus_x"
		private const val TRANSFORM_MINUS_Y_KEY = "transform_minus_y"
		private const val TRANSFORM_PLUS_X_KEY = "transform_plus_x"
		private const val TRANSFORM_PLUS_Y_KEY = "transform_plus_y"

		var layout = Layout()

		fun saveLayout(context: Context) {
			val preferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
			with(preferences.edit()) {
				putFloat(TRANSFORM_A_X_KEY, layout.transformA.position.x)
				putFloat(TRANSFORM_A_Y_KEY, layout.transformA.position.y)
				putFloat(TRANSFORM_B_X_KEY, layout.transformB.position.x)
				putFloat(TRANSFORM_B_Y_KEY, layout.transformB.position.y)
				putFloat(TRANSFORM_X_X_KEY, layout.transformX.position.x)
				putFloat(TRANSFORM_X_Y_KEY, layout.transformX.position.y)
				putFloat(TRANSFORM_Y_X_KEY, layout.transformY.position.x)
				putFloat(TRANSFORM_Y_Y_KEY, layout.transformY.position.y)
				putFloat(TRANSFORM_DPAD_X_KEY, layout.transformDPad.position.x)
				putFloat(TRANSFORM_DPAD_Y_KEY, layout.transformDPad.position.y)
				putFloat(TRANSFORM_LEFT_STICK_X_KEY, layout.transformLeftStickSocket.position.x)
				putFloat(TRANSFORM_LEFT_STICK_Y_KEY, layout.transformLeftStickSocket.position.y)
				putFloat(TRANSFORM_RIGHT_STICK_X_KEY, layout.transformRightStickSocket.position.x)
				putFloat(TRANSFORM_RIGHT_STICK_Y_KEY, layout.transformRightStickSocket.position.y)
				putFloat(TRANSFORM_L_X_KEY, layout.transformL.position.x)
				putFloat(TRANSFORM_L_Y_KEY, layout.transformL.position.y)
				putFloat(TRANSFORM_ZL_X_KEY, layout.transformZL.position.x)
				putFloat(TRANSFORM_ZL_Y_KEY, layout.transformZL.position.y)
				putFloat(TRANSFORM_R_X_KEY, layout.transformR.position.x)
				putFloat(TRANSFORM_R_Y_KEY, layout.transformR.position.y)
				putFloat(TRANSFORM_ZR_X_KEY, layout.transformZR.position.x)
				putFloat(TRANSFORM_ZR_Y_KEY, layout.transformZR.position.y)
				putFloat(TRANSFORM_MINUS_X_KEY, layout.transformMinus.position.x)
				putFloat(TRANSFORM_MINUS_Y_KEY, layout.transformMinus.position.y)
				putFloat(TRANSFORM_PLUS_X_KEY, layout.transformPlus.position.x)
				putFloat(TRANSFORM_PLUS_Y_KEY, layout.transformPlus.position.y)
				apply()
			}
		}

		fun loadLayout(context: Context) {
			val preferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
			val defaultLayout = Layout()
			layout = Layout(
				Transform(
					preferences.getFloat(TRANSFORM_A_X_KEY, defaultLayout.transformA.position.x),
					preferences.getFloat(TRANSFORM_A_Y_KEY, defaultLayout.transformA.position.y),
					defaultLayout.transformA.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_B_X_KEY, defaultLayout.transformB.position.x),
					preferences.getFloat(TRANSFORM_B_Y_KEY, defaultLayout.transformB.position.y),
					defaultLayout.transformB.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_X_X_KEY, defaultLayout.transformX.position.x),
					preferences.getFloat(TRANSFORM_X_Y_KEY, defaultLayout.transformX.position.y),
					defaultLayout.transformX.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_Y_X_KEY, defaultLayout.transformY.position.x),
					preferences.getFloat(TRANSFORM_Y_Y_KEY, defaultLayout.transformY.position.y),
					defaultLayout.transformY.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_DPAD_X_KEY, defaultLayout.transformDPad.position.x),
					preferences.getFloat(TRANSFORM_DPAD_Y_KEY, defaultLayout.transformDPad.position.y),
					defaultLayout.transformDPad.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_LEFT_STICK_X_KEY, defaultLayout.transformLeftStickSocket.position.x),
					preferences.getFloat(TRANSFORM_LEFT_STICK_Y_KEY, defaultLayout.transformLeftStickSocket.position.y),
					defaultLayout.transformLeftStickSocket.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_LEFT_STICK_X_KEY, defaultLayout.transformLeftStickSocket.position.x),
					preferences.getFloat(TRANSFORM_LEFT_STICK_Y_KEY, defaultLayout.transformLeftStickSocket.position.y),
					defaultLayout.transformLeftStickCap.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_RIGHT_STICK_X_KEY, defaultLayout.transformRightStickSocket.position.x),
					preferences.getFloat(TRANSFORM_RIGHT_STICK_Y_KEY, defaultLayout.transformRightStickSocket.position.y),
					defaultLayout.transformRightStickSocket.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_RIGHT_STICK_X_KEY, defaultLayout.transformRightStickSocket.position.x),
					preferences.getFloat(TRANSFORM_RIGHT_STICK_Y_KEY, defaultLayout.transformRightStickSocket.position.y),
					defaultLayout.transformRightStickCap.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_L_X_KEY, defaultLayout.transformL.position.x),
					preferences.getFloat(TRANSFORM_L_Y_KEY, defaultLayout.transformL.position.y),
					defaultLayout.transformL.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_ZL_X_KEY, defaultLayout.transformZL.position.x),
					preferences.getFloat(TRANSFORM_ZL_Y_KEY, defaultLayout.transformZL.position.y),
					defaultLayout.transformZL.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_R_X_KEY, defaultLayout.transformR.position.x),
					preferences.getFloat(TRANSFORM_R_Y_KEY, defaultLayout.transformR.position.y),
					defaultLayout.transformR.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_ZR_X_KEY, defaultLayout.transformZR.position.x),
					preferences.getFloat(TRANSFORM_ZR_Y_KEY, defaultLayout.transformZR.position.y),
					defaultLayout.transformZR.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_MINUS_X_KEY, defaultLayout.transformMinus.position.x),
					preferences.getFloat(TRANSFORM_MINUS_Y_KEY, defaultLayout.transformMinus.position.y),
					defaultLayout.transformMinus.size
				),
				Transform(
					preferences.getFloat(TRANSFORM_PLUS_X_KEY, defaultLayout.transformPlus.position.x),
					preferences.getFloat(TRANSFORM_PLUS_Y_KEY, defaultLayout.transformPlus.position.y),
					defaultLayout.transformPlus.size
				)
			)

		}
	}

	data class Transform(var position: PointF, val size: Float, var child: Transform? = null) {
		constructor(x: Float, y: Float, scale: Float) : this(PointF(x, y), scale)

		fun getRect(): RectF {
			val left = position.x - size
			val top = position.y + size
			val right = position.x + size
			val bottom = position.y - size
			return RectF(left, top, right, bottom)
		}

		fun clone(): Transform {
			return Transform(position.x, position.y, size)
		}
	}

	data class Layout(
		val transformA: Transform = Transform(950f, 200f, 100f),
		val transformB: Transform = Transform(800f, 50f, 100f),
		val transformX: Transform = Transform(800f, 350f, 100f),
		val transformY: Transform = Transform(650f, 200f, 100f),
		val transformDPad: Transform = Transform(-500f, -250f, 225f),
		val transformLeftStickSocket: Transform = Transform(-800f, 200f, 225f),
		val transformLeftStickCap: Transform = Transform(-800f, 200f, 100f),
		val transformRightStickSocket: Transform = Transform(500f, -250f, 225f),
		val transformRightStickCap: Transform = Transform(500f, -250f, 100f),
		val transformL: Transform = Transform(-900f, -150f, 125f),
		val transformZL: Transform = Transform(-850f, -350f, 125f),
		val transformR: Transform = Transform(900f, -150f, 125f),
		val transformZR: Transform = Transform(850f, -350f, 125f),
		val transformMinus: Transform = Transform(-250f, 200f, 75f),
		val transformPlus: Transform = Transform(250f, 200f, 75f)
	) {
		init {
			transformLeftStickSocket.child = transformLeftStickCap
			transformRightStickSocket.child = transformRightStickCap
		}

		fun clone(): Layout {
			return Layout(
				transformA.clone(),
				transformB.clone(),
				transformX.clone(),
				transformY.clone(),
				transformDPad.clone(),
				transformLeftStickSocket.clone(),
				transformLeftStickCap.clone(),
				transformRightStickSocket.clone(),
				transformRightStickCap.clone(),
				transformL.clone(),
				transformZL.clone(),
				transformR.clone(),
				transformZR.clone(),
				transformMinus.clone(),
				transformPlus.clone()
			)
		}

		companion object {
			fun getAlternativeLayout(): Layout {
				return Layout(
					transformA = Transform(950f, 200f, 100f),
					transformB = Transform(800f, 50f, 100f),
					transformX = Transform(800f, 350f, 100f),
					transformY = Transform(650f, 200f, 100f),
					transformDPad = Transform(-800f, 200f, 225f),
					transformLeftStickSocket = Transform(-700f, -250f, 225f),
					transformLeftStickCap = Transform(-700f, -250f, 100f),
					transformRightStickSocket = Transform(700f, -250f, 225f),
					transformRightStickCap = Transform(700f, -250f, 100f),
					transformL = Transform(-400f, -100f, 125f),
					transformZL = Transform(-350f, -350f, 125f),
					transformR = Transform(400f, -100f, 125f),
					transformZR = Transform(350f, -350f, 125f),
					transformMinus = Transform(-300f, 200f, 75f),
					transformPlus = Transform(300f, 200f, 75f)
				)
			}
		}
	}

	data class Pointer(var position: PointF, val controlledJoystick: Joystick?, val controlledTransform: Transform? = null, var delta: PointF = PointF(0f, 0f)) {
		constructor(position: PointF, controlledTransform: Transform?) : this(position, null, controlledTransform)
	}

	data class Button(val transform: Transform, val stateSetter: Consumer<Boolean>)

	data class Joystick(val transform: Transform, val margin: Float, val directionSetter: Consumer<PointF>, var controllingPointer: Pointer? = null) {
		val radius: Float
			get() = transform.size - margin
	}

	data class GamepadState(
		var a: Boolean = false,
		var b: Boolean = false,
		var x: Boolean = false,
		var y: Boolean = false,
		var dpadUp: Boolean = false,
		var dpadDown: Boolean = false,
		var dpadLeft: Boolean = false,
		var dpadRight: Boolean = false,
		var leftStick: PointF = PointF(0f, 0f),
		var rightStick: PointF = PointF(0f, 0f),
		var l: Boolean = false,
		var zl: Boolean = false,
		var r: Boolean = false,
		var zr: Boolean = false,
		var minus: Boolean = false,
		var plus: Boolean = false
	)
}
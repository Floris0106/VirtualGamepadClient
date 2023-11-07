package com.floris0106.virtualgamepadclient.net

import com.floris0106.virtualgamepadclient.view.GamepadView
import java.nio.ByteBuffer
import java.time.Instant

class GamepadStatePacket(private val state: GamepadView.GamepadState) : ServerboundPacket() {
	override val id: UByte = 0x01u

	private val timestamp = Instant.now()

	override fun getSize(): Int {
		return Short.SIZE_BYTES + Float.SIZE_BYTES * 4 + Long.SIZE_BYTES
	}

	override fun encode(buffer: ByteBuffer) {
		val buttons = listOf(
			state.a,
			state.b,
			state.x,
			state.y,
			state.dpadUp,
			state.dpadDown,
			state.dpadLeft,
			state.dpadRight,
			state.l,
			state.zl,
			state.r,
			state.zr,
			state.minus,
			state.plus
		)

		var buttonStates = 0
		for (i in buttons.indices)
			if (buttons[i])
				buttonStates = buttonStates or (1 shl i)

		buffer.putShort(buttonStates.toShort())
		buffer.putFloat(state.leftStick.x)
		buffer.putFloat(state.leftStick.y)
		buffer.putFloat(state.rightStick.x)
		buffer.putFloat(state.rightStick.y)

		buffer.putLong(timestamp.toEpochMilli())
	}
}
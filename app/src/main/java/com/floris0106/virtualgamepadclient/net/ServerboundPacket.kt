package com.floris0106.virtualgamepadclient.net

import java.nio.ByteBuffer

abstract class ServerboundPacket {
	abstract val id: UByte

	abstract fun getSize(): Int

	abstract fun encode(buffer: ByteBuffer)
}
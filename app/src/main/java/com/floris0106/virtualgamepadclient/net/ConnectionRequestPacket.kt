package com.floris0106.virtualgamepadclient.net

import java.nio.ByteBuffer
import java.util.UUID

class ConnectionRequestPacket : ServerboundPacket() {
    override val id: UByte = 0x00u

    private val token = UUID.randomUUID()

    override fun getSize(): Int {
        return Long.SIZE_BYTES * 2
    }

    override fun encode(buffer: ByteBuffer) {
        buffer.putLong(token.mostSignificantBits)
        buffer.putLong(token.leastSignificantBits)
    }
}
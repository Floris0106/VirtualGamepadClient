package com.floris0106.virtualgamepadclient.net

import java.nio.ByteBuffer

class ServerboundHeartbeatPacket : ServerboundPacket() {
    override val id: UByte = 0xFFu

    override fun getSize(): Int {
        return 0
    }

    override fun encode(buffer: ByteBuffer) {

    }
}
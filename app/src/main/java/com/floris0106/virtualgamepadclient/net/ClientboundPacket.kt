package com.floris0106.virtualgamepadclient.net

abstract class ClientboundPacket {
    companion object {
        private val packetTypes = mapOf<Byte, (ByteArray) -> ClientboundPacket>(
            0x00.toByte() to { data -> ConnectionIdPacket(data) },
            0xFF.toByte() to { _ -> ClientboundHeartbeatPacket() }
        )

        fun decode(bytes: ByteArray): ClientboundPacket {
            val data = bytes.sliceArray(1 until bytes.size)
            return packetTypes[bytes[0]]!!(data)
        }
    }
}
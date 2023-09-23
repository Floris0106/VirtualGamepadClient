package com.floris0106.virtualgamepadclient.net

class ConnectionIdPacket(data: ByteArray) : ClientboundPacket() {
    val connectionId: Byte = data[0]
}
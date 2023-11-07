package com.floris0106.virtualgamepadclient.net

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant
import java.util.concurrent.LinkedBlockingQueue

class ServerConnection(private val remoteAddress: InetAddress) {
	private val socket = DatagramSocket(PORT)
	private val sendQueue = LinkedBlockingQueue<ServerboundPacket>()

	private var connectionId: Byte = 0
	private var lastHeartbeat: Instant? = null
	private var isClosed = false

	val connected: Boolean
		get() = connectionId != 0.toByte()
	var timeoutListener: Runnable? = null

	private val sendThread = Thread {
		val connectionRequest = ConnectionRequestPacket()
		while (!Thread.interrupted()) {
			val packet: ServerboundPacket
			try {
				packet = if (connectionId == 0.toByte()) connectionRequest else sendQueue.take()
			} catch (e: InterruptedException) {
				return@Thread
			}

			val buffer = ByteBuffer.allocate(packet.getSize() + 2).order(ByteOrder.LITTLE_ENDIAN)
			buffer.put(connectionId).put(packet.id.toByte())
			packet.encode(buffer)
			val bytes = buffer.array()

			Log.d("ServerConnection", "Sending packet to $remoteAddress")
			try {
				socket.send(DatagramPacket(bytes, bytes.size, remoteAddress, PORT))
			} catch (e: SocketException) {
				close()
				return@Thread
			}

			if (connectionId == 0.toByte())
				try {
					Thread.sleep(100)
				} catch (e: InterruptedException) {
					return@Thread
				}
		}
	}

	private val receiveThread = Thread {
		val bytes = ByteArray(1024)
		val datagram = DatagramPacket(bytes, bytes.size)
		while (!Thread.interrupted()) {
			try {
				socket.receive(datagram)
			} catch (e: Exception) {
				close()
				return@Thread
			}

			handlePacket(ClientboundPacket.decode(bytes))
		}
	}

	private val heartbeatThread = Thread {
		while (!Thread.interrupted()) {
			if (lastHeartbeat != null && Instant.now().minusSeconds(5).isAfter(lastHeartbeat)) {
				Log.i("ServerConnection", "Connection timed out")
				timeoutListener?.run()
				close()
				return@Thread
			}

			try {
				Thread.sleep(100)
			} catch (e: InterruptedException) {
				return@Thread
			}
		}
	}

	init {
		sendThread.start()
		receiveThread.start()
		heartbeatThread.start()
	}

	fun send(packet: ServerboundPacket) {
		sendQueue.add(packet)
	}

	private fun handlePacket(packet: ClientboundPacket) {
		when (packet) {
			is ConnectionIdPacket -> {
				connectionId = packet.connectionId
				Log.i("ServerConnection", "Received connection ID: $connectionId")
			}

			is HeartbeatPacket -> {
				lastHeartbeat = Instant.now()
			}
		}
	}

	fun close() {
		if (isClosed)
			return
		isClosed = true

		sendThread.interrupt()
		receiveThread.interrupt()
		heartbeatThread.interrupt()

		socket.close()

		Log.i("ServerConnection", "Connection closed")
	}

	companion object {
		const val PORT = 1055

		private var instance: ServerConnection? = null

		fun create(ip: InetAddress): ServerConnection {
			instance?.close()
			instance = ServerConnection(ip)
			return instance!!
		}

		fun get(): ServerConnection? {
			return instance
		}
	}
}
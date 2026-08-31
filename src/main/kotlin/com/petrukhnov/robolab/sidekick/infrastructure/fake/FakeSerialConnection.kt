package com.petrukhnov.robolab.sidekick.infrastructure.fake

import com.petrukhnov.robolab.sidekick.infrastructure.SerialConnection
import java.util.concurrent.LinkedBlockingQueue

class FakeSerialConnection(
    linesToRead: List<String> = emptyList()
) : SerialConnection {
    private var connected = false
    private val queue = LinkedBlockingQueue<String>()

    init {
        queue.addAll(linesToRead)
    }

    override fun connect() {
        connected = true
    }

    override fun disconnect() {
        connected = false
    }

    override fun isConnected(): Boolean = connected

    override fun readLine(): String? {
        if (!connected) return null
        return queue.poll()
    }
    
    fun simulateInput(line: String) {
        queue.put(line)
    }
}

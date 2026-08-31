package com.petrukhnov.robolab.sidekick.infrastructure

interface SerialConnection : AutoCloseable {
    fun connect()
    fun disconnect()
    fun isConnected(): Boolean
    fun readLine(): String?

    override fun close() = disconnect()
}

class SerialException(message: String, cause: Throwable? = null) : Exception(message, cause)
class PortNotFoundException(port: String) : Exception("Serial port $port was not found.")
class PermissionDeniedException(port: String) : Exception("Permission denied for serial port $port.")

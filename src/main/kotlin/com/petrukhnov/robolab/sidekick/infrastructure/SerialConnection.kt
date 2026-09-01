package com.petrukhnov.robolab.sidekick.infrastructure

import kotlinx.serialization.Serializable

@Serializable
data class PortInfo(
    val systemPortName: String,
    val descriptivePortName: String,
    val portDescription: String,
    val isOpen: Boolean
)

interface SerialConnection : AutoCloseable {
    fun connect()
    fun disconnect()
    fun isConnected(): Boolean
    fun readLine(): String?

    override fun close() = disconnect()
}

interface PortProvider {
    fun listPorts(): List<PortInfo>
}

class SerialException(message: String, cause: Throwable? = null) : Exception(message, cause)
class PortNotFoundException(port: String) : Exception("Serial port $port was not found.")
class PermissionDeniedException(port: String) : Exception("Permission denied for serial port $port.")

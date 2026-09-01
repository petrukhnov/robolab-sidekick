package com.petrukhnov.robolab.sidekick.infrastructure.serial

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortTimeoutException
import com.petrukhnov.robolab.sidekick.infrastructure.PortNotFoundException
import com.petrukhnov.robolab.sidekick.infrastructure.SerialConnection
import com.petrukhnov.robolab.sidekick.infrastructure.SerialException
import java.io.BufferedReader
import java.io.InputStreamReader

class JSerialCommConnection(
    private val portName: String,
    private val baudRate: Int
) : SerialConnection {
    private var comPort: SerialPort? = null
    private var reader: BufferedReader? = null

    override fun connect() {
        if (comPort != null) return

        val port = SerialPort.getCommPort(portName)
        port.baudRate = baudRate
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
        
        if (!port.openPort()) {
            val ports = SerialPort.getCommPorts()
            if (ports.none { it.systemPortName == portName || it.descriptivePortName == portName }) {
                throw PortNotFoundException(portName)
            }
            throw SerialException("Failed to open port $portName. It might be in use or you might not have permissions.")
        }
        
        comPort = port
        reader = BufferedReader(InputStreamReader(port.inputStream))
    }

    override fun disconnect() {
        try {
            reader?.close()
        } catch (e: Exception) {
            // Ignore
        }
        comPort?.closePort()
        comPort = null
        reader = null
    }

    override fun isConnected(): Boolean {
        return comPort?.isOpen == true
    }

    override fun readLine(): String? {
        return try {
            reader?.readLine()
        } catch (e: SerialPortTimeoutException) {
            null
        } catch (e: Exception) {
            throw SerialException("Error reading from $portName", e)
        }
    }
}

package com.petrukhnov.robolab.sidekick.infrastructure.serial

import com.fazecast.jSerialComm.SerialPort
import com.petrukhnov.robolab.sidekick.infrastructure.PortInfo
import com.petrukhnov.robolab.sidekick.infrastructure.PortProvider

class JSerialCommPortProvider : PortProvider {
    override fun listPorts(): List<PortInfo> {
        return SerialPort.getCommPorts().map { port ->
            PortInfo(
                systemPortName = port.systemPortName,
                descriptivePortName = port.descriptivePortName,
                portDescription = port.portDescription,
                isOpen = port.isOpen
            )
        }
    }
}

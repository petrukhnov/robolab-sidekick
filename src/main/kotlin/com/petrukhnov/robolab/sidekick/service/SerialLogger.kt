package com.petrukhnov.robolab.sidekick.service

import com.petrukhnov.robolab.sidekick.config.AppConfig
import com.petrukhnov.robolab.sidekick.domain.LogEntry
import com.petrukhnov.robolab.sidekick.domain.LoggerStatus
import com.petrukhnov.robolab.sidekick.infrastructure.LogWriter
import com.petrukhnov.robolab.sidekick.infrastructure.SerialConnection
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class SerialLogger(
    private val serialConnection: SerialConnection,
    private val logWriter: LogWriter,
    private val config: AppConfig,
    private val clock: Clock = Clock.systemUTC()
) {
    private val log = LoggerFactory.getLogger(SerialLogger::class.java)
    private val running = AtomicBoolean(false)
    private val sequenceNumber = AtomicLong(0)
    private var readerThread: Thread? = null

    fun start() {
        if (running.getAndSet(true)) {
            throw IllegalStateException("Logger is already running")
        }

        log.info("Starting serial logger for port ${config.serialPort}")
        serialConnection.connect()

        readerThread = Thread({
            readLoop()
        }, "serial-reader-${config.serialPort}").apply {
            start()
        }
    }

    private fun readLoop() {
        try {
            while (running.get()) {
                val line = try {
                    serialConnection.readLine()
                } catch (e: Exception) {
                    log.error("Error reading from serial port", e)
                    null
                }

                if (line != null) {
                    val entry = LogEntry(
                        timestamp = Instant.now(clock),
                        sequence = sequenceNumber.incrementAndGet(),
                        source = config.logSource,
                        rawMessage = line
                    )
                    logWriter.append(entry)
                    logWriter.flush()
                } else {
                    // Small sleep to avoid tight loop if readLine is non-blocking and returns null
                    Thread.sleep(10)
                }
            }
        } catch (e: InterruptedException) {
            log.info("Serial reader thread interrupted")
        } finally {
            log.info("Serial reader thread exiting")
        }
    }

    fun stop() {
        if (!running.getAndSet(false)) {
            log.warn("Logger was not running")
            return
        }

        log.info("Stopping serial logger")
        readerThread?.interrupt()
        readerThread?.join(1000)
        
        serialConnection.disconnect()
        logWriter.close()
    }

    fun isRunning(): Boolean = running.get()

    fun getSequenceNumber(): Long = sequenceNumber.get()

    fun getStatus(): LoggerStatus {
        return if (running.get()) {
            LoggerStatus.Running(config.serialPort, sequenceNumber.get())
        } else {
            LoggerStatus.Idle
        }
    }
}

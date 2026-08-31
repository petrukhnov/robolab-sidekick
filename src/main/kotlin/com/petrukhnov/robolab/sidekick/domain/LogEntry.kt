package com.petrukhnov.robolab.sidekick.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@Serializable
data class LogEntry(
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    val sequence: Long,
    val source: String,
    val rawMessage: String
)

sealed class LoggerStatus {
    data object Idle : LoggerStatus()
    data class Running(val port: String, val sequence: Long) : LoggerStatus()
    data class Error(val message: String) : LoggerStatus()
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}

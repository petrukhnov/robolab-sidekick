package com.petrukhnov.robolab.sidekick.config

data class AppConfig(
    val serialPort: String,
    val baudRate: Int = 115000,
    val storagePath: String = "logs",
    val logSource: String = "arduino"
)

package me.clearedSpore.sporeCore.features.eco.`object`


data class EconomyLog(
    val action: EcoAction,
    val amount: Double,
    val reason: String,
    val timestamp: Long
)

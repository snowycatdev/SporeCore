package me.clearedSpore.sporeCore.features.mode.`object`

import org.bukkit.GameMode
import org.bukkit.Location

data class ModeData(
    val mode: Mode,
    val inventoryId: String?,
    val previousGamemode: GameMode,
    val previousLocation: Location? = null,
    val previousFlight: Boolean = false,
    val previousInvulnerable: Boolean = false
)

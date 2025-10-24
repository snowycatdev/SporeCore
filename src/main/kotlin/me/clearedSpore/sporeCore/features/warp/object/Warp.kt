package me.clearedSpore.sporeCore.features.warp.`object`

import org.bukkit.Location


data class Warp(
    val name: String,
    val permission: String? = null,
    val location: Location
)

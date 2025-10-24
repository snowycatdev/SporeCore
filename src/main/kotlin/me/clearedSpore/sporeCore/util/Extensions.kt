package me.clearedSpore.sporeCore.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack


object Extensions {

    fun ItemStack?.isNullOrAir(): Boolean = this == null || type == Material.AIR

}
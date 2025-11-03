package me.clearedSpore.sporeCore.features.kit.`object`

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.UUID


data class Kit(
    val name: String,
    val id: String,
    val inventory: List<ItemStack?>,
    val armor: List<ItemStack?>,
    val offHand: ItemStack? = null,
    val permission: String? = null,
    val cooldown: Long? = null,
    val displayItem: Material? = Material.CHEST,
)

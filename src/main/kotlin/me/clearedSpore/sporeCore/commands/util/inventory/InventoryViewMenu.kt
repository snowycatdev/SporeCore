package me.clearedSpore.sporeCore.commands.util.inventory

import me.clearedSpore.sporeAPI.menu.util.inventory.AbstractPlayerInventoryMenu
import me.clearedSpore.sporeAPI.menu.util.inventory.UtilInventoryMenu
import me.clearedSpore.sporeAPI.menu.util.inventory.UtilItem
import me.clearedSpore.sporeAPI.util.CC.blue
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class InventoryViewMenu(
    viewer: Player,
    target: Player,
    editable: Boolean = false,
    autoRefresh: Boolean = false,
    utilItems: MutableList<UtilItem>? = null,
    plugin: JavaPlugin
) : AbstractPlayerInventoryMenu(viewer, target, editable, autoRefresh, utilItems, plugin) {

    override val title: String get() = "Inventory of ${target.name.blue()}"
}
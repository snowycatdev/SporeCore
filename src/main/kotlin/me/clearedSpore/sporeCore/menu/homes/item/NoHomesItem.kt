package me.clearedSpore.sporeCore.menu.homes.item

import me.clearedSpore.sporeAPI.menu.Item
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.warp.`object`.Warp
import me.clearedSpore.sporeCore.menu.confirm.ConfirmMenu
import me.clearedSpore.sporeCore.util.Perm
import me.clearedSpore.sporeCore.util.TeleportService.awaitTeleport
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class NoHomesItem() : Item() {

    override fun createItem(): ItemStack {
        val item = ItemStack(Material.BARRIER)
        val meta = item.itemMeta

        meta?.setDisplayName("No homes".red())

        val lore = mutableListOf<String>()
        lore.add("You don't have any homes!".white())

        meta?.lore = lore
        item.itemMeta = meta
        return item
    }

    override fun onClickEvent(clicker: Player, clickType: ClickType) {}
}

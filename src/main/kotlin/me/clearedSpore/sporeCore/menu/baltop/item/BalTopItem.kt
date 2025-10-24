package me.clearedSpore.sporeCore.menu.baltop.item

import me.clearedSpore.sporeAPI.menu.Item
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.white
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class BalTopItem(
    private val rank: Int,
    private val playerName: String,
    private val balance: Double
) : Item() {

    override fun createItem(): ItemStack {
        val skull = ItemStack(Material.PLAYER_HEAD)
        val meta = skull.itemMeta as SkullMeta

        meta.setDisplayName("#$rank ".white() + playerName.blue())

        val lore = mutableListOf<String>()
        lore.add("Balance: ".white() + "%.2f".format(balance).green())

        meta.lore = lore

        val offline = Bukkit.getOfflinePlayer(playerName)
        meta.owningPlayer = offline

        skull.itemMeta = meta
        return skull
    }

    override fun onClickEvent(clicker: Player, clickType: ClickType) {
    }
}

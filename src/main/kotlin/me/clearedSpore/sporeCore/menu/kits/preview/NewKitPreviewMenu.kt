package me.clearedSpore.sporeCore.menu.kits.preview

import me.clearedSpore.sporeAPI.menu.util.inventory.AbstractInventoryMenu
import me.clearedSpore.sporeCore.SporeCore
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class NewKitPreviewMenu(
    viewer: Player,
    inventoryItems: Array<ItemStack?>,
    armorItems: Array<ItemStack?>,
    offhand: ItemStack?,
    customTitle: String,
) : AbstractInventoryMenu(
    viewer = viewer,
    editable = false,
    autoRefresh = false,
    utilItems = null,
    plugin = SporeCore.instance
) {

    override val title: String = customTitle

    override val inventoryContents: Array<ItemStack?> = inventoryItems
    override val armorContents: Array<ItemStack?> = armorItems
    override val offhandItem: ItemStack? = offhand

    override fun onInventoryUpdate(
        contents: Array<ItemStack?>,
        armor: Array<ItemStack?>,
        offhand: ItemStack?
    ) {
    }
}
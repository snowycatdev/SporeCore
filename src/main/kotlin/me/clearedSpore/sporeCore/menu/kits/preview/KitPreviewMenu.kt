package me.clearedSpore.sporeCore.menu.kits.preview

import me.clearedSpore.sporeAPI.menu.Item
import me.clearedSpore.sporeAPI.menu.Menu
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.kit.`object`.Kit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class KitPreviewMenu(
    private val kit: Kit
) : Menu(SporeCore.instance) {

    override fun fillEmptySlots() = true

    override fun getMenuName(): String = "Preview: ${kit.name.blue()}"

    override fun getRows(): Int = 6

    override fun setMenuItems() {
        var index = 0

        for (y in 1..4) {
            for (x in 1..9) {
                val item = kit.inventory.getOrNull(index) ?: ItemStack(Material.AIR)
                setMenuItem(x, y, SimpleItem(item))
                index++
            }
        }


        for (x in 1..4) {
            val armorItem = kit.armor.getOrNull(x - 1) ?: ItemStack(Material.AIR)
            setMenuItem(x, 5, SimpleItem(armorItem))
        }

        val offhandItem = kit.offHand ?: ItemStack(Material.AIR)
        setMenuItem(6, 5, SimpleItem(offhandItem))
    }

    private class SimpleItem(private val stack: ItemStack) : Item() {
        override fun createItem(): ItemStack = stack
        override fun onClickEvent(clicker: Player, clickType: ClickType) { }
    }
}

package me.clearedSpore.sporeCore.menu.kits

import me.clearedSpore.sporeAPI.menu.BasePaginatedMenu
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.kit.KitService
import me.clearedSpore.sporeCore.menu.kits.item.KitItem
import me.clearedSpore.sporeCore.menu.kits.item.NoKitsItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class KitsMenu(private val player: Player) : BasePaginatedMenu(SporeCore.instance, true) {

    private val kitService: KitService = SporeCore.instance.kitService

    override fun getMenuName(): String = "Kits"
    override fun getRows(): Int = 6

    override fun createItems() {
        val kits = kitService.getAllKits()

        val accessibleKits = kits.filter { it.permission == null || player.hasPermission(it.permission) }

        if (accessibleKits.isEmpty()) {
            setGlobalMenuItem(5, 3, NoKitsItem())
            return
        }

        accessibleKits.forEach { kit ->
            addItem(KitItem(kit, player))
        }
    }


    override fun onInventoryClickEvent(
        clicker: Player,
        clickType: ClickType,
        event: InventoryClickEvent
    ) {}
}

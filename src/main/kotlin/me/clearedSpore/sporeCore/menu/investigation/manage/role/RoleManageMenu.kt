package me.clearedSpore.sporeCore.menu.investigation.manage.role

import me.clearedSpore.sporeAPI.menu.BasePaginatedMenu
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.investigation.IGService
import me.clearedSpore.sporeCore.features.investigation.`object`.Investigation
import me.clearedSpore.sporeCore.menu.investigation.manage.ManageIGMenu
import me.clearedSpore.sporeCore.menu.investigation.manage.role.item.AddPlayerItem
import me.clearedSpore.sporeCore.menu.investigation.manage.role.item.RoleItem
import me.clearedSpore.sporeCore.menu.util.BackItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class RoleManageMenu(
    val investigationID: String,
    val viewer: Player
) : BasePaginatedMenu(SporeCore.instance, true) {

    override fun getMenuName(): String {
        return "Investigation | Staff"
    }

    override fun getRows(): Int {
        return 6
    }

    override fun createItems() {
        val investigation = IGService.findInvestigation(investigationID) ?: return
        val admins = investigation.admin
        val staff = investigation.staff

        admins.forEach { admin ->
            addItem(RoleItem(investigation, viewer, admin, RoleType.ADMIN))
        }

        staff.forEach { staff ->
            addItem(RoleItem(investigation, viewer, staff, RoleType.STAFF))
        }

        setGlobalMenuItem(4, 6, AddPlayerItem(investigation, viewer))
        setGlobalMenuItem(6, 6, BackItem {
            ManageIGMenu(investigationID, viewer).open(viewer)
        })
    }

    override fun onInventoryClickEvent(
        clicker: Player,
        clickType: ClickType,
        event: InventoryClickEvent
    ) {}


    enum class RoleType {
        STAFF,
        ADMIN
    }
}
package me.clearedSpore.sporeCore.menu.reports.report

import me.clearedSpore.sporeAPI.menu.BasePaginatedMenu
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.reports.`object`.ReportType
import me.clearedSpore.sporeCore.menu.reports.report.item.CustomReasonItem
import me.clearedSpore.sporeCore.menu.reports.report.item.ReportTypeItem
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.atan


class ReportTypeMenu(
    val target: OfflinePlayer
) : BasePaginatedMenu(SporeCore.instance, true) {

    override fun getMenuName(): String {
        return "Report ${target.name} | Type"
    }

    override fun getRows(): Int {
        return 6
    }

    override fun createItems() {
        ReportType.values().forEach { type ->
            if(type != ReportType.CUSTOM) {
                addItem(ReportTypeItem(target, type))
            }
        }

        if(SporeCore.instance.coreConfig.reports.allowCustom){
            addItem(CustomReasonItem(target))
        }
    }

    override fun onInventoryClickEvent(
        clicker: Player,
        clickType: ClickType,
        event: InventoryClickEvent
    ) {}
}
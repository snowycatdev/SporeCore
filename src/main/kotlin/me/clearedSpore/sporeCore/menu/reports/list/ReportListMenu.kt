package me.clearedSpore.sporeCore.menu.reports.list

import me.clearedSpore.sporeAPI.menu.BasePaginatedMenu
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.reports.ReportService
import me.clearedSpore.sporeCore.features.reports.`object`.Report
import me.clearedSpore.sporeCore.features.reports.`object`.ReportStatus
import me.clearedSpore.sporeCore.menu.reports.list.item.FilterItem
import me.clearedSpore.sporeCore.menu.reports.list.item.NoReportsItem
import me.clearedSpore.sporeCore.menu.reports.list.item.ReportItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent


class ReportListMenu(
    val viewer: Player,
    val filter: ReportStatus = ReportStatus.PENDING
) : BasePaginatedMenu(SporeCore.instance, false) {

    override fun getMenuName(): String {
        return "Reports | ${filter.displayName}"
    }

    override fun getRows(): Int {
        return 4
    }

    override fun createItems() {
        startAutoRefresh()
        val reports = ReportService.reportCollection
            .find()
            .mapNotNull { Report.fromDocument(it) }
            .filter { it.status == filter }
            .sortedByDescending { it.timestamp }

        setGlobalMenuItem(5, 1, FilterItem(filter))

        if(reports.isNotEmpty()) {
            reports.forEach { report ->
                addItem(ReportItem(report, viewer))
            }
        } else {
            setGlobalMenuItem(5, 3, NoReportsItem())
        }
    }


    override fun onInventoryClickEvent(
        clicker: Player,
        clickType: ClickType,
        event: InventoryClickEvent
    ) {}
}
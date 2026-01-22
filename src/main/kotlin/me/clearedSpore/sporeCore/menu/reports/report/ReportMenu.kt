package me.clearedSpore.sporeCore.menu.reports.report

import me.clearedSpore.sporeAPI.menu.BasePaginatedMenu
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.StringUtil.firstPart
import me.clearedSpore.sporeAPI.util.StringUtil.hasFlag
import me.clearedSpore.sporeAPI.util.StringUtil.splitPipe
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.reports.`object`.ReportType
import me.clearedSpore.sporeCore.menu.reports.list.item.ReportItem
import me.clearedSpore.sporeCore.menu.reports.report.item.ReasonItem
import me.clearedSpore.sporeCore.menu.util.BackItem
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent


class ReportMenu(
    val viewer: Player,
    val target: OfflinePlayer,
    val type: ReportType
) : BasePaginatedMenu(SporeCore.instance, true) {



    override fun getMenuName(): String {
        return "Report ${target.name} | Reason"
    }

    override fun getRows(): Int {
        return 6
    }

    override fun createItems() {
        val reasons = SporeCore.instance.coreConfig.reports.reportReasons

        setGlobalMenuItem(5, 6, BackItem({
            ReportTypeMenu(target).open(viewer)
        }))

        reasons.forEach { reasonRaw ->
            val parts = reasonRaw.splitPipe()
            val reason = parts.firstOrNull() ?: reasonRaw
            val typeRaw = parts.getOrNull(1)

            val reasonType = try {
                if (typeRaw.isNullOrBlank()) {
                    Logger.warn("Invalid reason type $reasonRaw -> Adding reason to other")
                    ReportType.OTHER
                } else {
                    ReportType.valueOf(typeRaw.uppercase())
                }
            } catch (ex: IllegalArgumentException) {
                Logger.warn("Invalid reason type $reasonRaw -> Adding reason to other")
                ReportType.OTHER
            }

            if (reasonType == type) {
                addItem(ReasonItem(reason, reasonType, target))
            }
        }
    }



    override fun onInventoryClickEvent(
        clicker: Player,
        clickType: ClickType,
        event: InventoryClickEvent
    ) {}
}
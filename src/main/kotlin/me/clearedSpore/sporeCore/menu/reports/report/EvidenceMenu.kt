package me.clearedSpore.sporeCore.menu.reports.report

import me.clearedSpore.sporeAPI.menu.Menu
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.reports.ReportService
import me.clearedSpore.sporeCore.features.reports.`object`.ReportType
import me.clearedSpore.sporeCore.menu.reports.report.item.evidence.EvidenceItem
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


class EvidenceMenu(
    val target: OfflinePlayer,
    val reason: String,
    val type: ReportType
) : Menu(SporeCore.instance) {

    init {
        shouldReopen = true
    }

    override fun getMenuName(): String {
        return "Do you have evidence?"
    }

    override fun getRows(): Int {
        return 3
    }

    override fun setMenuItems() {
        setMenuItem(3, 2, EvidenceItem(true, target, reason, type, this))
        setMenuItem(7, 2, EvidenceItem(false, target, reason, type, this))
    }
}
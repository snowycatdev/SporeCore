package me.clearedSpore.sporeCore.menu.stat

import me.clearedSpore.sporeAPI.menu.Menu
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.menu.stat.item.BalanceItem
import me.clearedSpore.sporeCore.menu.stat.item.PlaytimeItem
import me.clearedSpore.sporeCore.menu.stat.item.StatItem
import me.clearedSpore.sporeCore.menu.stat.item.WeeklyPlaytimeItem
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Statistic
import org.bukkit.entity.Player


class StatsMenu(val player: OfflinePlayer) : Menu(SporeCore.instance) {
    override fun getMenuName(): String {
       return "Stats |  ${player.name}"
    }

    override fun getRows(): Int {
        return 3
    }

    override fun setMenuItems() {

        setMenuItem(3, 2, PlaytimeItem(player))
        setMenuItem(4, 2, WeeklyPlaytimeItem(player))
        setMenuItem(5, 2, StatItem(player, Statistic.PLAYER_KILLS, "Kills", Material.IRON_SWORD))
        setMenuItem(6, 2, BalanceItem(player))
        setMenuItem(7, 2, StatItem(player, Statistic.DEATHS, "Deaths", Material.SKELETON_SKULL))
    }
}
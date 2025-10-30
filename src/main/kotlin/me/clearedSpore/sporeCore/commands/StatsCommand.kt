package me.clearedSpore.sporeCore.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import co.aikar.commands.annotation.Syntax
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.menu.stat.StatsMenu
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("stat|stats|checkstats")
class StatsCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    @Syntax("[player]")
    fun onStats(player: Player, @Optional targetName: String?) {
        if (targetName == null) {
            StatsMenu(player).open(player)
            return
        }

        val target = Bukkit.getOfflinePlayer(targetName)

        if(!target.hasPlayedBefore()){
            player.userJoinFail()
            return
        }

        if (player != target && !player.hasPermission(Perm.STATS_OTHERS)) {
            player.sendMessage("You don't have permission to view other players' stats.".red())
            return
        }

        StatsMenu(target).open(player)

    }

    
}
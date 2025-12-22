package me.clearedSpore.sporeCore.commands.utilitymenus

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("workbench|craft|crafting")
@CommandPermission(Perm.WORKBENCH)
class WorkbenchCommand() : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onWorkbench(player: Player, @Optional targetName: String?) {
        if (targetName == null) {
            player.openWorkbench(player.location, true)
            return
        }

        if (!player.hasPermission(Perm.UTILITY_OTHERS)) {
            player.sendMessage("You don't have permission to do this!".red())
            return
        }

        val target = Bukkit.getPlayer(targetName)
        target!!.openWorkbench(player.location, true)
        player.sendMessage("You have opened a crafting table for ${target.name}".blue())

    }
}
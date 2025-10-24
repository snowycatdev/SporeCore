package me.clearedSpore.sporeCore.commands.utilitymenus

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("anvil")
@CommandPermission(Perm.ANVIL)
class AnvilCommand() : BaseCommand() {

    @CommandCompletion("@players")
    @Default
    fun onAnvil(player: Player, @Optional targetName: String?) {
        if (targetName == null) {
            player.openAnvil(player.location, true)
            return
        }

        if (!player.hasPermission(Perm.UTILITY_OTHERS)) {
            player.sendMessage("You don't have permission to do this!".red())
            return
        }

        val target = Bukkit.getPlayer(targetName)
        if (target == null) {
            player.sendMessage("That player is not online!".red())
            return
        }

        target.openAnvil(target.location, true)
        player.sendMessage("You have opened an anvil for ${target.name}".blue())
    }

}
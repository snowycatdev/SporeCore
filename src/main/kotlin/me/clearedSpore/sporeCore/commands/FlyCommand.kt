package me.clearedSpore.sporeCore.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("fly|togglefly")
@CommandPermission(Perm.FLIGHT)
class FlyCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    @Syntax("[player]")
    fun onFly(sender: CommandSender, @Optional targetName: String?) {
        val target: Player? = when {
            sender is Player && targetName == null -> sender
            targetName != null -> Bukkit.getPlayer(targetName)
            else -> null
        }

        if (target == null) {
            sender.sendMessage("That player is not online!".red())
            return
        }

        if (sender != target && !sender.hasPermission(Perm.FLIGHT_OTHERS)) {
            sender.sendMessage("You don't have permission to toggle other players their flight".red())
            return
        }

        val flyEnabled: Boolean = target.allowFlight
        target.allowFlight = !flyEnabled

        if (sender == target) {
            sender.sendMessage("Your flight has been ${if (flyEnabled) "disabled" else "enabled"}".blue())
        } else {
            sender.sendMessage("You have ${if (flyEnabled) "disabled" else "enabled"} ${target.name}'s flight.".blue())
            target.sendMessage("Your flight has been ${if (flyEnabled) "disabled" else "enabled"}".blue())
        }
    }

    @Subcommand("*")
    @CommandCompletion("true|false")
    @CommandPermission(Perm.FLIGHT_OTHERS)
    fun onAll(sender: CommandSender, status: Boolean) {

        Bukkit.getOnlinePlayers().forEach { player ->
            player.allowFlight = status
            player.sendMessage("Your flight has been ${if (status) "disabled" else "enabled"}".blue())
        }

        val players = Bukkit.getOnlinePlayers().size -1

        sender.sendMessage("Successfully ${if (status) "disabled" else "enabled"} flight for $players players.".blue())
    }
}

package me.clearedSpore.sporeCore.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("invulnerable|god")
@CommandPermission(Perm.GOD)
class GodCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    @Syntax("[player]")
    fun onGodMode(sender: CommandSender, @Optional targetName: String?) {
        val target: Player? = when {
            sender is Player && targetName == null -> sender
            targetName != null -> Bukkit.getPlayer(targetName)
            else -> null
        }

        if (target == null) {
            sender.sendMessage("That player is not online!".red())
            return
        }

        if (sender != target && !sender.hasPermission(Perm.GOD_OTHERS)) {
            sender.sendMessage("You don't have permission to toggle other players their godmode".red())
            return
        }

        val godmodeEnabled: Boolean = target.isInvulnerable
        target.isInvulnerable = !godmodeEnabled

        if (sender == target) {
            sender.sendMessage("Your godmode has been ${if (godmodeEnabled) "enabled" else "disabled"}".blue())
        } else {
            sender.sendMessage("You have ${if (godmodeEnabled) "enabled" else "disabled"} godmode for ${target.name}.".blue())
            target.sendMessage("Your godmode has been ${if (godmodeEnabled) "enabled" else "disabled"}".blue())
        }
    }

    @Subcommand("*")
    @CommandCompletion("true|false")
    @CommandPermission(Perm.GOD_OTHERS)
    fun onAll(sender: CommandSender, status: Boolean) {

        Bukkit.getOnlinePlayers().forEach { player ->
            player.isInvulnerable = status
            player.sendMessage("Your godmode has been ${if (status) "enabled" else "disabled"}".blue())
        }

        val players = Bukkit.getOnlinePlayers().size -1

        sender.sendMessage("Successfully ${if (status) "enabled" else "disabled"} godmode for $players players.".blue())
    }
}

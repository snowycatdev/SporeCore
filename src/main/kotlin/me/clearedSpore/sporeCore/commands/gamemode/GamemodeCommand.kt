package me.clearedSpore.sporeCore.commands.gamemode

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.util.Perm
import me.clearedSpore.sporeAPI.util.StringUtil.capitalizeFirstLetter
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("gamemode|gm")
@CommandPermission(Perm.GAMEMODE)
class GamemodeCommand : BaseCommand() {

    @Default
    @Syntax("<gamemode> <player>")
    @CommandCompletion("@gamemodes @players|*")
    fun onGamemode(sender: CommandSender, gamemode: String, @Optional targetName: String?) {

        if (sender !is Player && targetName == null) {
            sender.sendMessage("You must specify a player name when running this command from console.".red())
            return
        }

        val mode = try {
            GameMode.valueOf(gamemode.uppercase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("Invalid gamemode! Use creative, survival, spectator, or adventure.".red())
            return
        }


        val permission = when (mode) {
            GameMode.CREATIVE -> Perm.CREATIVE
            GameMode.SURVIVAL -> Perm.SURVIVAL
            GameMode.SPECTATOR -> Perm.SPECTATOR
            GameMode.ADVENTURE -> Perm.ADVENTURE
        }

        if (!sender.hasPermission(permission)) {
            sender.sendMessage("You do not have permission to switch to ${mode.name.lowercase().capitalizeFirstLetter()}!".red())
            return
        }

        val targets: List<Player> = when {
            targetName == null && sender is Player -> listOf(sender)
            targetName == "*" -> {
                if (!sender.hasPermission(Perm.GAMEMODE_OTHERS)) {
                    sender.sendMessage("You do not have permission to change everyone’s gamemode!".red())
                    return
                }
                Bukkit.getOnlinePlayers().toList()
            }
            targetName != null -> {
                val target = Bukkit.getPlayerExact(targetName)
                if (target == null) {
                    sender.sendMessage("That player is not online!".red())
                    return
                }
                listOf(target)
            }
            else -> {
                sender.sendMessage("Invalid usage!".red())
                return
            }
        }

        targets.forEach { it.gameMode = mode }

        if (targetName == "*") {
            Logger.log(sender, Perm.LOG, "set everyone’s gamemode to ${mode.name.capitalizeFirstLetter()}", false)
            sender.sendMessage("You set everyone’s gamemode to ${mode.name.capitalizeFirstLetter()}.".blue())
        } else {
            val target = targets.first()
            Logger.log(sender, Perm.LOG, "changed ${target.name}’s gamemode to ${mode.name.capitalizeFirstLetter()}", false)
            sender.sendMessage(
                "You updated ".blue() +
                        target.name.white() +
                        "’s gamemode to ${mode.name.capitalizeFirstLetter()}.".blue()
            )
        }
    }
}

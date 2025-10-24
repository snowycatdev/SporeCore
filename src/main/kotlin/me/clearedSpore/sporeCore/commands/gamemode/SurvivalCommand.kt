package me.clearedSpore.sporeCore.commands.gamemode

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("survival|gms")
@CommandPermission(Perm.SURVIVAL)
class SurvivalCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    @Syntax("<player>")
    fun onSurvival(sender: CommandSender, @Optional targetName: String?) {
        if (sender !is Player && targetName == null) {
            sender.sendMessage("You must specify a player name when running this command from console.".red())
            return
        }

        val target: Player? = when {
            sender is Player && targetName == null -> sender
            targetName != null -> Bukkit.getPlayerExact(targetName)
            else -> null
        }

        if (target == null) {
            sender.sendMessage("That player is not online!".red())
            return
        }

        target.gameMode = GameMode.SURVIVAL

        if (sender == target) {
            Logger.log(sender, Perm.LOG, "changed their gamemode to Survival", false)
        } else {
            Logger.log(sender, Perm.LOG, "changed ${target.name}’s gamemode to Survival", false)
        }
        sender.sendMessage(
            "You updated ".blue() +
                    target.name.white() +
                    "’s gamemode to Survival.".blue()
        )
    }

    @Subcommand("*")
    @CommandPermission(Perm.GAMEMODE_OTHERS)
    fun onAll(sender: CommandSender) {
        Bukkit.getOnlinePlayers().forEach { it.gameMode = GameMode.SURVIVAL }
        Logger.log(sender, Perm.LOG, "set everyone’s gamemode to Survival", false)
        sender.sendMessage("You set everyone’s gamemode to Survival.".blue())
    }
}

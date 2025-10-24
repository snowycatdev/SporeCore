package me.clearedSpore.sporeCore.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("feed")
@CommandPermission(Perm.FEED)
class FeedCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    @Syntax("<player>")
    fun onHeal(sender: CommandSender, @Optional targetName: String?) {

        if (sender !is Player && targetName == null) {
            sender.sendMessage("You must specify a player name when running this command from console.".red())
            return
        }

        val target: Player? = when {
            sender is Player && targetName == null -> sender
            targetName != null -> Bukkit.getPlayer(targetName)
            else -> null
        }

        if (target == null) {
            sender.sendMessage("That player is not online!".red())
            return
        }

        if(target.name != sender.name && !sender.hasPermission(Perm.FEED_OTHERS)){
            sender.sendMessage("You don't have permission to feed other players".red())
            return
        }

        target.foodLevel = 20
        target.saturation = 20f

        Logger.log(sender, Perm.LOG, "fed ${target.name}", false)
        sender.sendMessage("You have fed ".blue() + target.name + ".".blue())
    }

    @Subcommand("*")
    @CommandPermission(Perm.FEED_OTHERS)
    fun onAll(sender: CommandSender) {

        Bukkit.getOnlinePlayers().forEach { player ->
            player.foodLevel = 20
            player.saturation = 20f
        }

        Logger.log(sender, Perm.LOG, "fed everyone", false)
        sender.sendMessage("You fed everyone.".blue())
    }
}

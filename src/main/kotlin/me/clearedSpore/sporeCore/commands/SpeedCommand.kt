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
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeCore.extension.Extensions.isNullOrAir
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("speed")
@CommandPermission(Perm.SPEED)
class SpeedCommand : BaseCommand() {

    @Default
    @CommandCompletion("@range:1-10 @players")
    @Syntax("<player>")
    fun onSpeed(sender: CommandSender, speed: Float, @Optional targetName: String?) {
        val target: Player? = when {
            sender is Player && targetName == null -> sender
            targetName != null -> Bukkit.getPlayer(targetName)
            else -> null
        }

        if (target == null) {
            sender.sendMessage("That player is not online!".red())
            return
        }

        if (sender != target && !sender.hasPermission(Perm.SPEED_OTHERS)) {
            sender.sendMessage("You don't have permission to set other players their speed.".red())
            return
        }

        if (speed !in 1.0..10.0) {
            sender.sendMessage("Amount must be between 1 - 10!".red())
            return
        }

        val bukkitSpeed = speed / 10f
        target.flySpeed = bukkitSpeed
        target.walkSpeed = bukkitSpeed

        if (sender == target) {
            sender.sendMessage("Your speed has been set to &f$speed".blue())
        } else {
            sender.sendMessage("You have set ${target.name}'s speed to &f$speed.".blue())
            target.sendMessage("Your speed has been set to &f$speed".blue())
        }
    }

    @Subcommand("*")
    @CommandCompletion("@range:1-10")
    @CommandPermission(Perm.SPEED_OTHERS)
    fun onAll(sender: CommandSender, speed: Float) {

        if(speed !in 1.0..10.0){
            sender.sendMessage("Amount must be between 1 - 10!".red())
            return
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            val bukkitSpeed = speed / 10f
            player.flySpeed = bukkitSpeed
            player.walkSpeed = bukkitSpeed
            player.sendMessage("Your speed has been set to &f$speed".blue())
        }

        sender.sendMessage("Successfully repaired set everyone their speed to &f$speed".blue())
    }

    @Subcommand("reset")
    @Syntax("[player|*]")
    @CommandCompletion("*|@players")
    fun onReset(sender: CommandSender, @Optional targetName: String?) {
        when {
            targetName == null && sender is Player -> {
                sender.flySpeed = 0.1f
                sender.walkSpeed = 0.2f
                sender.sendMessage("Your speed has been reset to default.".blue())
            }

            targetName == "*" -> {
                if (!sender.hasPermission(Perm.SPEED_OTHERS)) {
                    sender.sendMessage("You don't have permission to reset others' speed.".red())
                    return
                }

                Bukkit.getOnlinePlayers().forEach { player ->
                    player.flySpeed = 0.1f
                    player.walkSpeed = 0.2f
                    player.sendMessage("Your speed has been reset to default.".blue())
                }

                sender.sendMessage("Reset speed for all players.".blue())
            }

            else -> {
                val target = Bukkit.getPlayer(targetName!!)
                if (target == null) {
                    sender.sendMessage("That player is not online!".red())
                    return
                }

                if (sender != target && !sender.hasPermission(Perm.SPEED_OTHERS)) {
                    sender.sendMessage("You don't have permission to reset others' speed.".red())
                    return
                }

                target.flySpeed = 0.1f
                target.walkSpeed = 0.2f

                sender.sendMessage("You reset ${target.name}'s speed to default.".blue())
                target.sendMessage("Your speed has been reset to default.".blue())
            }
        }
    }
}
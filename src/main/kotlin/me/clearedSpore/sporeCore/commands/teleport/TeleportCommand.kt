package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("tp|teleport|goto")
@CommandPermission(Perm.TELEPORT)
class TeleportCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players|~ @players|~ ~ ~")
    fun onTp(sender: CommandSender, arg1: String, @Optional arg2: String?, @Optional arg3: String?, @Optional arg4: String?) {
        if (sender !is Player && arg2 == null) {
            sender.sendMessage("Console must specify both arguments.".red())
            return
        }

        val player = sender as? Player

        when {
            Bukkit.getPlayer(arg1) != null && arg2 == null -> {
                val target = Bukkit.getPlayer(arg1)!!
                if (player == null) {
                    sender.sendMessage("Console must specify a target to teleport.".red())
                    return
                }

                player.teleport(target.location)
                Logger.log(sender, Perm.LOG, "teleported to ${target.name}", false)
                sender.sendSuccessMessage("Teleported to ${target.name}.")
            }

            Bukkit.getPlayer(arg1) != null && Bukkit.getPlayer(arg2 ?: "") != null -> {
                val target1 = Bukkit.getPlayer(arg1)!!
                val target2 = Bukkit.getPlayer(arg2!!)!!

                if (!sender.hasPermission(Perm.TELEPORT_OTHERS)) {
                    sender.sendMessage("You don't have permission to teleport other players!".red())
                    return
                }

                target1.teleport(target2.location)
                Logger.log(sender, Perm.LOG, "teleported ${target1.name} to ${target2.name}", false)
                sender.sendMessage("Teleported ${target1.name} to ${target2.name}.".red())
            }

            arg3 != null && arg4 == null -> {
                val target = if (arg1.toDoubleOrNull() == null) Bukkit.getPlayer(arg1) else player
                val coordArgs = if (target == player) listOf(arg1, arg2, arg3) else listOf(arg2, arg3, arg4)

                if (target == null) {
                    sender.sendMessage("That player is not online.".red())
                    return
                }

                if (!sender.hasPermission(Perm.TELEPORT_CORDS)) {
                    sender.sendMessage("You don't have permission to use coordinate teleport!".red())
                    return
                }

                val base = target.location
                val x = parseCoordinate(base.x, coordArgs[0].toString())?.let { String.format("%.1f", it).toDouble() }
                val y = parseCoordinate(base.y, coordArgs[1].toString())?.let { String.format("%.1f", it).toDouble() }
                val z = parseCoordinate(base.z, coordArgs[2].toString())?.let { String.format("%.1f", it).toDouble() }

                if (x == null || y == null || z == null) {
                    sender.sendMessage("Invalid coordinates.".red())
                    return
                }

                target.teleport(Location(base.world, x, y, z))
                Logger.log(sender, Perm.LOG, "teleported ${target.name} to coordinates $x $y $z", false)
                sender.sendMessage("Teleported ${target.name} to &f$x $y $z".blue())
            }

            else -> sender.sendMessage("Invalid usage: /tp <player> | <x> <y> <z> | <player1> <player2> | <player> <x> <y> <z>".red())
        }
    }

    private fun parseCoordinate(base: Double, input: String): Double? {
        return try {
            if (input.startsWith("~")) {
                if (input == "~") base else base + input.substring(1).toDouble()
            } else input.toDouble()
        } catch (e: Exception) {
            null
        }
    }
}

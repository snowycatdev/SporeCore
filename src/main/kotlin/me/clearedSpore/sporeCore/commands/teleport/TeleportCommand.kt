package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Logger
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
    fun onTp(
        sender: CommandSender,
        arg1: String,
        @Optional arg2: String?,
        @Optional arg3: String?,
        @Optional arg4: String?
    ) {
        val player = sender as? Player

        when {
            arg2 == null -> {
                val target = Bukkit.getPlayer(arg1)
                if (target == null) {
                    sender.sendMessage("That player is not online.".red())
                    return
                }

                if (player == null) {
                    sender.sendMessage("Console must specify a target to teleport.".red())
                    return
                }

                player.teleport(target.location)
                Logger.log(sender, Perm.LOG, "teleported to ${target.name}", false)
                sender.sendMessage("Teleported to ${target.name}.".blue())
            }

            arg2 != null && arg3 == null -> {
                val target1 = Bukkit.getPlayer(arg1)
                val target2 = Bukkit.getPlayer(arg2)

                if (target1 == null || target2 == null) {
                    sender.sendMessage("One of the specified players is not online.".red())
                    return
                }

                if (!sender.hasPermission(Perm.TELEPORT_OTHERS)) {
                    sender.sendMessage("You don't have permission to teleport other players!".red())
                    return
                }

                target1.teleport(target2.location)
                Logger.log(sender, Perm.LOG, "teleported ${target1.name} to ${target2.name}", false)
                sender.sendMessage("Teleported ${target1.name} to ${target2.name}.".blue())
            }

            arg2 != null && arg3 != null && arg4 == null -> {
                if (player == null) {
                    sender.sendMessage("Console must specify a player for coordinate teleport.".red())
                    return
                }

                if (!sender.hasPermission(Perm.TELEPORT_CORDS)) {
                    sender.sendMessage("You don't have permission to use coordinate teleport!".red())
                    return
                }

                val loc = parseCoordinates(player.location, arg1, arg2, arg3) ?: run {
                    sender.sendMessage("Invalid coordinates.".red())
                    return
                }

                val x = parseCoordinate(loc.x, arg1)?.let { String.format("%.1f", it).toDouble() }
                val y = parseCoordinate(loc.y, arg2)?.let { String.format("%.1f", it).toDouble() }
                val z = parseCoordinate(loc.z, arg3)?.let { String.format("%.1f", it).toDouble() }

                player.teleport(loc)
                Logger.log(sender, Perm.LOG, "teleported ${player.name} to coordinates $x $y $z", false)
                sender.sendMessage("Teleported to &f$x $y $z".blue())
            }

            arg2 != null && arg3 != null && arg4 != null -> {
                val target = Bukkit.getPlayer(arg1)
                if (target == null) {
                    sender.sendMessage("That player is not online.".red())
                    return
                }

                if (!sender.hasPermission(Perm.TELEPORT_CORDS)) {
                    sender.sendMessage("You don't have permission to teleport players to coordinates!".red())
                    return
                }

                val loc = parseCoordinates(target.location, arg2, arg3, arg4) ?: run {
                    sender.sendMessage("Invalid coordinates.".red())
                    return
                }

                val x = parseCoordinate(loc.x, arg2)?.let { String.format("%.1f", it).toDouble() }
                val y = parseCoordinate(loc.y, arg3)?.let { String.format("%.1f", it).toDouble() }
                val z = parseCoordinate(loc.z, arg4)?.let { String.format("%.1f", it).toDouble() }


                target.teleport(loc)
                Logger.log(sender, Perm.LOG, "teleported ${target.name} to coordinates $x $y $z", false)
                sender.sendMessage("Teleported ${target.name} to &f$x $y $z".blue())
            }

            else -> sender.sendMessage("Invalid usage: /tp <player> | <player1> <player2> | <x> <y> <z> | <player> <x> <y> <z>".red())
        }
    }

    private fun parseCoordinates(base: Location, xArg: String, yArg: String, zArg: String): Location? {
        val world = base.world ?: return null

        val x = parseCoordinate(base.x, xArg) ?: return null
        val y = parseCoordinate(base.y, yArg) ?: return null
        val z = parseCoordinate(base.z, zArg) ?: return null

        return Location(world, x, y, z)
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

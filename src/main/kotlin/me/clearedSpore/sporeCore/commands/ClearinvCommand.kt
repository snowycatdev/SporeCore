package me.clearedSpore.sporeCore.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Confirmation
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("clearinv|clearinventory|ci|clear")
class ClearinvCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    @Syntax("[player]")
    fun onInvClear(sender: CommandSender, @Optional targetName: String?) {
        val target: Player? = when {
            sender is Player && targetName == null -> sender
            targetName != null -> Bukkit.getPlayer(targetName)
            else -> null
        }

        if (target == null) {
            sender.sendMessage("That player is not online!".red())
            return
        }

        if (sender != target && !sender.hasPermission(Perm.CLEAR_OTHERS)) {
            sender.sendMessage("You don't have permission to clear other players' inventories.".red())
            return
        }


        if (sender == target) {
            if(target.gameMode.equals(GameMode.CREATIVE) || Confirmation.isPlayerPending(target.uniqueId)){
                sender.sendMessage("Your inventory has been cleared.".blue())
                target.inventory.clear()
                Confirmation.removePlayer(target.uniqueId)
            } else {
                Confirmation.addPlayer(target.uniqueId)
            }
        } else {
            target.inventory.clear()
            sender.sendMessage("You have cleared ${target.name}'s inventory.".blue())
            target.sendMessage("Your inventory has been cleared.".blue())
        }
    }

    @Subcommand("*")
    @CommandPermission(Perm.CLEAR_OTHERS)
    fun onAll(sender: CommandSender) {
        var cleared = 0

        Bukkit.getOnlinePlayers().forEach { player ->
            player.inventory.clear()
            cleared++
            player.sendMessage("Your inventory has been cleared.".blue())
        }

        sender.sendMessage("Successfully cleared inventories of $cleared players.".blue())
    }
}

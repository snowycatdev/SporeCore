package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("tpall|teleportall")
@CommandPermission(Perm.TELEPORT_ALL)
class TpAllCommand : BaseCommand() {

    @Default
    fun onTpAll(sender: Player) {
        Bukkit.getOnlinePlayers().forEach {
            if (it != sender) it.teleport(sender.location)
        }

        val amount = Bukkit.getOnlinePlayers().size - 1

        Logger.log(sender, Perm.LOG, "teleported ${amount} players themself", false)
        sender.sendSuccessMessage("Teleported everyone to you.")
    }
}

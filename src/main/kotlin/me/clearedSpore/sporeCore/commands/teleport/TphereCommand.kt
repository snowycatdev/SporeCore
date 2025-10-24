package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.entity.Player

@CommandAlias("tphere|teleporthere|bring")
@CommandPermission(Perm.TELEPORT_OTHERS)
class TphereCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onTphere(sender: Player, target: Player?) {
        if (target == null || !target.isOnline) {
            sender.sendErrorMessage("That player is not online.")
            return
        }

        Logger.log(sender, Perm.LOG, "teleported ${target.name} to themself", false)
        target.teleport(sender.location)
        sender.sendSuccessMessage("Teleported ${target.name} to you.")
    }
}

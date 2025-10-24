package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("tpahere")
class TpaHereCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onTpaHere(sender: Player, targetName: String) {
        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendErrorMessage("That player is not online!")
            return
        }


        TeleportRequestService.sendRequest(sender, target, TeleportRequestService.RequestType.TPAHERE)
    }
}

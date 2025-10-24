package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("tpa")
class TpaCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onTpa(sender: Player, targetName: String) {
        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendErrorMessage("That player is not online!")
            return
        }

        TeleportRequestService.sendRequest(sender, target, TeleportRequestService.RequestType.TPA)
    }
}

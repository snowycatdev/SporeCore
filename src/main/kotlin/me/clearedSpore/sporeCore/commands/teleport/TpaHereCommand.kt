package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("tpahere")
class TpaHereCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onTpaHere(sender: Player, target: OnlinePlayer) {
        TeleportRequestService.sendRequest(sender, target.player, TeleportRequestService.RequestType.TPAHERE)
    }
}

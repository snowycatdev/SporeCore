package me.clearedSpore.sporeCore.commands.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import org.bukkit.entity.Player

@CommandAlias("tpa")
class TpaCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onTpa(sender: Player, target: OnlinePlayer) {
        TeleportRequestService.sendRequest(sender, target.player, TeleportRequestService.RequestType.TPA)
    }
}

package me.clearedSpore.sporeCore.commands.moderation

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.features.punishment.PunishmentService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("clearchat")
@CommandPermission(Perm.CLEAR_CHAT)
class ClearChatCommand : BaseCommand() {


    @Default
    fun onClear(sender: CommandSender) {
        val lines = PunishmentService.config.settings.clearLines

        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(Perm.CLEAR_CHAT_BYPASS)) {
                repeat(lines) {
                    player.sendMessage(" ")
                }
            }
        }

        Logger.log(sender, Perm.LOG, "cleared the chat", true)
    }
}
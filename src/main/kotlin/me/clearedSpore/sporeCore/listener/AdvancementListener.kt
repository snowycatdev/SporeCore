package me.clearedSpore.sporeCore.listener

import me.clearedSpore.sporeAPI.exception.LoggedException
import me.clearedSpore.sporeAPI.util.Webhook
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.annotations.AutoListener
import me.clearedSpore.sporeCore.features.discord.DiscordService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent


class AdvancementListener : Listener {
    @EventHandler
    fun onAdvancement(event: PlayerAdvancementDoneEvent) {
        val player = event.player
        val config = SporeCore.instance.coreConfig
        val advancementType = event.advancement.displayName()

        if (config.discord.chat.isNotEmpty()) {
            val embed = Webhook.Embed()
                .setColor(0x00FF00)
                .setDescription("**${player.name}** has completed the advancement **${advancementType}**")

            val webhook = Webhook(config.discord.chat)
                .setProfileURL(DiscordService.getAvatarURL(player.uniqueId))
                .setUsername(player.name)
                .addEmbed(embed)

            try {
                webhook.send()
            } catch (ex: Exception) {
                throw LoggedException(
                    userMessage = "Failed to send message to Discord.",
                    internalMessage = "Failed to send message to Discord",
                    channel = LoggedException.Channel.GENERAL,
                    developerOnly = false,
                    cause = ex
                ).also { it.log() }
            }
        }
    }
}
package me.clearedSpore.sporeCore.features.setting.impl

import me.clearedSpore.sporeCore.CoreConfig
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.type.ToggleSetting
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material

@Setting
class ChannelMessagesSetting : ToggleSetting(
    key = "channel-messages",
    displayName = "Channel messages",
    item = Material.PAPER,
    lore = listOf(
        "If you want to receive a message when someone",
        "types in a chat channel"
    ),
    permission = Perm.CHANNEL_ALLOW
) {
    override fun defaultValue(): Boolean = true
    override fun isEnabledInConfig(config: CoreConfig): Boolean = config.features.channels
}
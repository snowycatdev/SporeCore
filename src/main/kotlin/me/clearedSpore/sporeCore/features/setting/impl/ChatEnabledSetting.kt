package me.clearedSpore.sporeCore.features.setting.impl

import me.clearedSpore.sporeCore.CoreConfig
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.type.ToggleSetting
import org.bukkit.Material


@Setting
class ChatEnabledSetting : ToggleSetting(
    key = "chat-enabled",
    displayName = "Chat enabled",
    item = Material.PAPER,
    lore = listOf(
        "If you can see chat messages sent by players",
        "Staff will bypass this!"
    )
) {
    override fun defaultValue(): Boolean = true

    override fun isEnabledInConfig(config: CoreConfig): Boolean = true
}

package me.clearedSpore.sporeCore.features.setting.impl

import me.clearedSpore.sporeCore.CoreConfig
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.type.ToggleSetting
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material

@Setting
class TryLogSetting : ToggleSetting(
    key = "try-logs",
    displayName = "Try logs",
    item = Material.BELL,
    lore = listOf(
        "If you want to receive a message when someone",
        "tries to join while being banned."
    ),
    permission = Perm.PUNISH_LOG
) {
    override fun defaultValue(): Boolean = true
    override fun isEnabledInConfig(config: CoreConfig): Boolean = config.features.punishments
}
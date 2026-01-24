package me.clearedSpore.sporeCore.features.setting.impl

import me.clearedSpore.sporeCore.CoreConfig
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.type.ToggleSetting
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material

@Setting
class PunishmentLogsSetting : ToggleSetting(
    key = "punishment-logs",
    displayName = "Punishment logs",
    item = Material.DISPENSER,
    lore = listOf(
        "If you can see punishment logs."
    ),
    permission = Perm.PUNISH_LOG
) {
    override fun defaultValue(): Boolean = true
    override fun isEnabledInConfig(config: CoreConfig): Boolean = config.features.punishments
}
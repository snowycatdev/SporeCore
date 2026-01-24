package me.clearedSpore.sporeCore.features.setting.impl

import me.clearedSpore.sporeCore.CoreConfig
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.type.ToggleSetting
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material

@Setting
class StaffmodeOnJoinSetting : ToggleSetting(
    key = "staffmode-on-join",
    displayName = "Staff mode on join",
    item = Material.COMPASS,
    lore = listOf(
        "If you want to be put in staffmode",
        "when you join the server"
    ),
    permission = Perm.MODE_ALLOW
) {
    override fun defaultValue(): Boolean = true
    override fun isEnabledInConfig(config: CoreConfig): Boolean = config.features.modes
}
package me.clearedSpore.sporeCore.features.setting.model.type

import me.clearedSpore.sporeCore.features.setting.model.AbstractSetting
import org.bukkit.Material
import org.bukkit.entity.Player

abstract class ToggleSetting(
    key: String,
    displayName: String,
    item: Material,
    lore: List<String>,
    permission: String? = null
) : AbstractSetting<Boolean>(key, displayName, item, lore, permission) {

    override fun get(userValue: Any?): Boolean {
        return userValue as? Boolean ?: defaultValue()
    }

    override fun serialize(value: Boolean): Any = value

    override fun onClick(player: Player, current: Boolean): Boolean {
        return !current
    }
}

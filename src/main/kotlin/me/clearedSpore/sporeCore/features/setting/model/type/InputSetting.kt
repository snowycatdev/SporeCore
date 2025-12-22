package me.clearedSpore.sporeCore.features.setting.model.type

import me.clearedSpore.sporeCore.features.setting.model.AbstractSetting
import org.bukkit.Material
import org.bukkit.entity.Player

abstract class InputSetting<T>(
    key: String,
    displayName: String,
    item: Material,
    lore: List<String>,
    permission: String? = null
) : AbstractSetting<T>(key, displayName, item, lore, permission) {

    override fun get(userValue: Any?): T {
        return userValue as? T ?: defaultValue()
    }

    override fun serialize(value: T): Any = value as Any

    override fun onClick(player: Player, current: T): T? {
        openInput(player)
        return null
    }

    abstract fun openInput(player: Player)
}

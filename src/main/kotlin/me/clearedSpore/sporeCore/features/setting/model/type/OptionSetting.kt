package me.clearedSpore.sporeCore.features.setting.model.type

import me.clearedSpore.sporeCore.features.setting.model.AbstractSetting
import org.bukkit.Material
import org.bukkit.entity.Player

abstract class OptionSetting<T>(
    key: String,
    displayName: String,
    item: Material,
    lore: List<String>,
    permission: String? = null
) : AbstractSetting<T>(key, displayName, item, lore, permission) {

    abstract fun values(): List<T>

    override fun get(userValue: Any?): T {
        val deserialized = if (userValue != null) deserialize(userValue) else null
        return values().find { it == deserialized } ?: defaultValue()
    }

    override fun serialize(value: T): Any {
        return if (value is Enum<*>) value.name else value as Any
    }

    open fun deserialize(value: Any): T {
        val firstValue = values().firstOrNull() ?: return defaultValue()
        return if (firstValue is Enum<*> && value is String) {
            @Suppress("UNCHECKED_CAST")
            values().firstOrNull { (it as Enum<*>).name == value } ?: defaultValue()
        } else {
            value as? T ?: defaultValue()
        }
    }

    override fun onClick(player: Player, current: T): T {
        val list = values()
        val index = list.indexOf(current)
        return if (index == -1 || index + 1 >= list.size) list.first() else list[index + 1]
    }
}

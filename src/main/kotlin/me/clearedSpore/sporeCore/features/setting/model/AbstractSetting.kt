package me.clearedSpore.sporeCore.features.setting.model

import me.clearedSpore.sporeCore.CoreConfig
import me.clearedSpore.sporeCore.features.setting.SkullData
import org.bukkit.Material
import org.bukkit.entity.Player

abstract class AbstractSetting<T>(
    val key: String,
    val displayName: String,
    val item: Material,
    val lore: List<String>,
    val permission: String? = null
) {
    private var skullData: SkullData? = null

    fun setSkullData(data: SkullData): AbstractSetting<T> {
        skullData = data
        return this
    }

    fun getSkullData(): SkullData? = skullData

    abstract fun defaultValue(): T
    abstract fun isEnabledInConfig(config: CoreConfig): Boolean
    abstract fun get(userValue: Any?): T
    abstract fun serialize(value: T): Any
    abstract fun onClick(player: Player, current: T): T?
}

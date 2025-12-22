package me.clearedSpore.sporeCore.features.setting.impl

import me.clearedSpore.sporeCore.CoreConfig
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.type.ToggleSetting
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material

@Setting
class CurrencyLogsSetting : ToggleSetting(
    key = "currency-logs",
    displayName = "Currency logs",
    item = Material.GOLD_INGOT,
    lore = listOf(
        "If you can see the custom currency logs."
    ),
    permission = Perm.CURRENCY_NOTIFY
) {
    override fun defaultValue(): Boolean = true
    override fun isEnabledInConfig(config: CoreConfig): Boolean = config.features.currency.enabled
}
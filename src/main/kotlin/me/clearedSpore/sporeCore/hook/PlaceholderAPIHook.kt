package me.clearedSpore.sporeCore.hook

import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.TimeUtil
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.currency.CurrencySystemService
import me.clearedSpore.sporeCore.features.eco.EconomyService
import me.clearedSpore.sporeCore.features.eco.`object`.BalanceFormat
import me.clearedSpore.sporeCore.features.vanish.VanishService
import me.clearedSpore.sporeCore.user.UserManager
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player


class PlaceholderAPIHook() : PlaceholderExpansion() {

    override fun getIdentifier(): String = "sporecore"
    override fun getAuthor(): String = "ClearedSpore"
    override fun getVersion(): String = "2.0"
    override fun persist() = true

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        val user = UserManager.get(player) ?: return null

        val args = params.lowercase().split("_")
        val config = SporeCore.instance.coreConfig
        val features = config.features

        return when {
            params.equals("balance_raw", ignoreCase = true) -> {
                if (!config.economy.enabled) return null
                user.balance.toString()
            }

            params.equals("balance_formatted", ignoreCase = true) -> {
                if (!config.economy.enabled) return null
                EconomyService.format(user.balance, BalanceFormat.COMPACT).toString()
            }

            params.equals("balance_decimal", ignoreCase = true) -> {
                if (!config.economy.enabled) return null
                EconomyService.format(user.balance, BalanceFormat.DECIMAL).toString()
            }

            params.equals("homes", ignoreCase = true) -> {
                if (!features.homes) return null
                SporeCore.instance.homeService.getAllHomes(user).size.toString()
            }

            args.size >= 3 && args[0] == "kit" && args[1] == "timeleft" -> {
                if (!features.kits) return null
                val kitName = args.drop(2).joinToString("_")
                val remaining = user.getKitCooldownRemaining(kitName)
                if (remaining <= 0) "Ready" else TimeUtil.formatDuration(remaining)
            }

            args.size >= 2 && args[1].equals("balance", ignoreCase = true) -> {
                if (!features.currency.enabled) return null
                val currencyToken = args[0].lowercase()
                val settings = CurrencySystemService.config.currencySettings
                val matchesCurrency = currencyToken == settings.pluralName.lowercase()
                        || currencyToken == settings.singularName.lowercase()
                if (!matchesCurrency) null
                else {
                    val formatToken = args.getOrNull(2)?.lowercase()
                    val formatType = when (formatToken) {
                        "plain" -> BalanceFormat.PLAIN
                        "decimal" -> BalanceFormat.DECIMAL
                        "compact" -> BalanceFormat.COMPACT
                        null -> runCatching { BalanceFormat.valueOf(settings.balanceFormat.uppercase()) }
                            .getOrDefault(BalanceFormat.PLAIN)

                        else -> runCatching { BalanceFormat.valueOf(settings.balanceFormat.uppercase()) }
                            .getOrDefault(BalanceFormat.PLAIN)
                    }
                    val balance = CurrencySystemService.getBalance(user)
                    CurrencySystemService.format(balance, formatType)
                }
            }

            params.equals("vanish_tag", ignoreCase = true) -> {
                if (!features.vanish) return null
                if (VanishService.isVanished(user.uuid)) {
                    val text = config.general.vanishTag
                    return text.translate()
                } else {
                    return ""
                }
            }

            params.equals("is_vanished", ignoreCase = true) -> {
                if (!features.vanish) return null
                return VanishService.isVanished(user.uuid).toString()
            }

            else -> null
        }
    }
}
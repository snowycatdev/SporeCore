package me.clearedSpore.sporeCore.hook

import me.clearedSpore.sporeAPI.util.TimeUtil
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.eco.EconomyService
import me.clearedSpore.sporeCore.features.eco.`object`.BalanceFormat
import me.clearedSpore.sporeCore.user.UserManager
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


class PlaceholderAPIHook() : PlaceholderExpansion() {

    override fun getIdentifier(): String = "sporecore"
    override fun getAuthor(): String = "ClearedSpore"
    override fun getVersion(): String = "1.0"
    override fun persist() = true

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        val user = UserManager.get(player) ?: return null

        val args = params.lowercase().split("_")

        return when {
            // %sporecore_balance_raw%
            params.equals("balance_raw", ignoreCase = true) -> user.balance.toString()

            // %sporecore_balance_formatted%
            params.equals("balance_formatted", ignoreCase = true) ->
                EconomyService.format(user.balance, BalanceFormat.COMPACT).toString()

            // %sporecore_balance_decimal%
            params.equals("balance_decimal", ignoreCase = true) ->
                EconomyService.format(user.balance, BalanceFormat.DECIMAL).toString()

            // %sporecore_homes%
            params.equals("homes", ignoreCase = true) ->
                SporeCore.instance.homeService.getAllHomes(user).size.toString()

            // %sporecore_kit_timeleft_<kitName>%
            args.size >= 3 && args[0] == "kit" && args[1] == "timeleft" -> {
                val kitName = args.drop(2).joinToString("_")
                val remaining = user.getKitCooldownRemaining(kitName)
                if (remaining <= 0) "Ready" else TimeUtil.formatDuration(remaining)
            }

            else -> null
        }
    }
}
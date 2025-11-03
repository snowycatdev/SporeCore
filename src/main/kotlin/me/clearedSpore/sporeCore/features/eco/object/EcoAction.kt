package me.clearedSpore.sporeCore.features.eco.`object`

import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.currency.CurrencySystemService
import me.clearedSpore.sporeCore.currency.`object`.CreditAction
import me.clearedSpore.sporeCore.currency.`object`.CreditAction.SPENT


enum class EcoAction {
    ADDED, REMOVED, SET;

    fun format(amount: Double): String {
        val formattedAmount = "%,.2f".format(amount)
        return when (this) {
            ADDED -> "+$formattedAmount".green()
            REMOVED -> "-$formattedAmount".red()
            SET -> formattedAmount.green()
        }
    }
}

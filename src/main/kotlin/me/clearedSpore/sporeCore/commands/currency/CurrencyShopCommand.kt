package me.clearedSpore.sporeCore.commands.currency

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeCore.features.currency.menu.main.CurrencyMainMenu
import org.bukkit.entity.Player

@CommandAlias("%currencyshopalias")
class CurrencyShopCommand : BaseCommand() {

    @Default()
    fun onShop(player: Player) {
        CurrencyMainMenu(player).open(player)
    }
}
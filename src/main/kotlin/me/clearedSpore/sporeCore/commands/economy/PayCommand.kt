package me.clearedSpore.sporeCore.commands.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.extension.PlayerExtension.userFail
import me.clearedSpore.sporeCore.features.eco.EconomyService
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("pay")
@CommandPermission(Perm.ECO)
class PayCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players @payamounts")
    @Syntax("<player> <amount>")
    fun onPay(sender: Player, targetName: String, amountStr: String) {
        if (!SporeCore.instance.coreConfig.economy.paying) {
            sender.sendErrorMessage("Paying other players is disabled.")
            return
        }

        val amount = EconomyService.parseAmount(amountStr) ?: return sender.sendMessage("Invalid amount!".red())

        val senderUser = UserManager.getOffline(Bukkit.getOfflinePlayer(sender.name).uniqueId)
        if (senderUser == null) { return sender.userFail() }

        val targetUser = UserManager.getOffline(Bukkit.getOfflinePlayer(targetName).uniqueId)
        if (targetUser == null) { return sender.userFail() }


        if (senderUser.balance < amount) {
            sender.sendErrorMessage("You do not have enough money.")
            return
        }

        EconomyService.remove(senderUser, amount, "Paid to ${targetUser.playerName}")
        EconomyService.add(targetUser, amount, "Received from ${sender.name}")

        val formattedAmount = EconomyService.format(amount)
        sender.sendMessage("You paid ${targetUser.playerName} ".blue() + formattedAmount.green())

        targetUser.player?.let { player ->
            if (player.isOnline) {
                player.sendMessage("You received ".blue() + formattedAmount.green() + " from ${sender.name}.".blue())
            } else {
                targetUser.pendingMessages.add(
                    "While you were away you received ".blue() + formattedAmount.green() + " from ${sender.name}".blue()
                )
            }
        }
    }
}

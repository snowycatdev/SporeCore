package me.clearedSpore.sporeCore.commands.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.extension.PlayerExtension.userFail
import me.clearedSpore.sporeCore.features.eco.EconomyService
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("eco|economy|bal|balance")
@CommandPermission(Perm.ECO)
class EconomyCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    @Syntax("[player]")
    fun onBalance(sender: CommandSender, @Optional targetName: String?) {
        val uuid = if (sender is Player && targetName == null) sender.uniqueId
        else Bukkit.getOfflinePlayer(targetName ?: return sender.userFail()).uniqueId

        val user = UserManager.getOffline(uuid)
        if (user == null) { return sender.userFail() }

        val offlinePlayer = Bukkit.getOfflinePlayer(user.playerId)
        val displayName = offlinePlayer.name ?: user.playerName.ifEmpty { "Unknown" }

        sender.sendMessage("${displayName}'s balance: ".blue() + EconomyService.format(user.balance).green())
    }

    @Subcommand("add")
    @CommandPermission(Perm.ECO_ADMIN)
    @CommandCompletion("@players @payamounts")
    @Syntax("<player> <amount>")
    fun onAdd(sender: CommandSender, targetName: String, amountStr: String) {
        val amount = EconomyService.parseAmount(amountStr) ?: return sender.sendMessage("Invalid amount!".red())

        val user = UserManager.getOffline(Bukkit.getOfflinePlayer(targetName).uniqueId)
        if (user == null) { return sender.userFail() }

        EconomyService.add(user, amount, "Added by ${sender.name}")
        user.save("balance", true)

        sender.sendMessage("Added ".blue() + EconomyService.format(amount).green() + " to ${user.playerName}.".blue())
    }

    @Subcommand("remove")
    @CommandPermission(Perm.ECO_ADMIN)
    @CommandCompletion("@players @payamounts")
    @Syntax("<player> <amount>")
    fun onRemove(sender: CommandSender, targetName: String, amountStr: String) {
        val amount = EconomyService.parseAmount(amountStr) ?: return sender.sendMessage("Invalid amount!".red())

        val user = UserManager.getOffline(Bukkit.getOfflinePlayer(targetName).uniqueId)
        if (user == null) { return sender.userFail() }

        EconomyService.remove(user, amount, "Removed by ${sender.name}")
        user.save("balance")

        sender.sendMessage("Removed ".blue() + EconomyService.format(amount).red() + " from ${user.playerName}.".blue())
    }

    @Subcommand("set")
    @CommandPermission(Perm.ECO_ADMIN)
    @CommandCompletion("@players @payamounts")
    @Syntax("<player> <amount>")
    fun onSet(sender: CommandSender, targetName: String, amountStr: String) {
        val amount = EconomyService.parseAmount(amountStr) ?: return sender.sendMessage("Invalid amount!".red())

        val user = UserManager.get(Bukkit.getOfflinePlayer(targetName).uniqueId)

        EconomyService.set(user, amount, "Set by ${sender.name}")
        user.save("balance")

        sender.sendMessage("Set ${user.playerName}'s balance to ".blue() + EconomyService.format(amount).green())
    }
}

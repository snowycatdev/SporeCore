package me.clearedSpore.sporeCore.commands.moderation

import co.aikar.commands.BaseCommand
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.extension.PlayerExtension.userFail
import me.clearedSpore.sporeCore.features.punishment.PunishmentService
import me.clearedSpore.sporeCore.features.punishment.`object`.PunishmentType
import me.clearedSpore.sporeCore.user.UserManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class AbstractPunishCommand(
    private val type: PunishmentType,
    private val requiresTime: Boolean = false
) : BaseCommand() {

    abstract val customPermission: String

    protected fun handle(
        sender: CommandSender,
        targetName: String,
        time: String?,
        reasonKey: String?
    ) {
        val target = Bukkit.getOfflinePlayer(targetName)
        val targetUser = UserManager.get(target) ?: run {
            sender.sendMessage("Could not find player $targetName".red())
            return
        }

        val punisher = if (sender is Player) UserManager.get(sender.uniqueId) else UserManager.getConsoleUser()

        if (punisher == null) {
            sender.userFail()
            return
        }

        val settings = PunishmentService.config.settings

        if (!settings.selfPunish && punisher.uuid == targetUser.uuid) {
            sender.sendMessage("You cannot punish yourself.".red())
            return
        }

        if (!settings.permBypass.isNullOrBlank() && targetUser.player?.hasPermission(settings.permBypass) == true) {
            sender.sendMessage("You cannot punish this player.".red())
            return
        }

        val reasonPair = reasonKey?.let { PunishmentService.findReasonDefinition(it) }
        val reasonDefinition = reasonPair?.second
        val isCustomReason = reasonDefinition == null

        if (settings.requireReason && isCustomReason && reasonKey.isNullOrBlank()) {
            sender.sendMessage("You must provide a valid reason.".red())
            return
        }

        if (isCustomReason && !sender.hasPermission(customPermission)) {
            sender.sendMessage("You do not have permission to provide a custom reason.".red())
            return
        }


        val (finalReason, finalTime) = if (reasonDefinition != null) {
            val pastOffenses = targetUser.punishments.count { it.offense.equals(reasonKey, ignoreCase = true) }
            val nextOffense = pastOffenses + 1
            val offenseEntry = reasonDefinition.offenses[nextOffense]
                ?: reasonDefinition.offenses[reasonDefinition.offenses.keys.maxOrNull()]!!

            offenseEntry.reason to offenseEntry.time
        } else {
            (reasonKey ?: "No reason provided") to time
        }

        try {
            PunishmentService.punish(
                targetUser = targetUser,
                punisher = punisher,
                rawReason = finalReason,
                providedType = type,
                providedTime = finalTime
            )

        } catch (ex: Exception) {
            sender.sendMessage("Failed to punish player: ${ex.message}".red())
            ex.printStackTrace()
        }
    }

}

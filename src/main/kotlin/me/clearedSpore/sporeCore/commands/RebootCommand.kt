package me.clearedSpore.sporeCore.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.features.reboot.RebootService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender


@CommandAlias("reboot|restart")
@CommandPermission(Perm.REBOOT)
class RebootCommand() : BaseCommand() {


    @Default
    @Syntax("<time>")
    fun onReboot(sender: CommandSender, time: String) {
        RebootService.startReboot(time)
    }

    @Subcommand("cancel")
    fun onCancel(sender: CommandSender) {
        RebootService.cancelReboot()
    }
}
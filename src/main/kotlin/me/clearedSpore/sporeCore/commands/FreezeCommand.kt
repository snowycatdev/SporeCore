package me.clearedSpore.sporeCore.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Name
import co.aikar.commands.annotation.Private
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

@CommandAlias("freeze")
class FreezeCommand : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onFreeze(sender: CommandSender, @Name("target") targetRaw: OnlinePlayer){
        val target = targetRaw.player

        if(target.hasMetadata("frozen")){
            target.removeMetadata("frozen", SporeCore.instance)
            Logger.log(sender, Perm.LOG, "unfrozen ${target.name}", true)
            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You are no longer frozen!!".blue())
            target.sendMessage("You may leave the server again.".blue())
            target.sendMessage("")
            target.sendMessage("")
        } else {
            target.setMetadata("frozen", FixedMetadataValue(SporeCore.instance, true))
            Logger.log(sender, Perm.LOG, "froze ${target.name}", true)
            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You have been &bFrozen".red())
            target.sendMessage("Do not leave the server!".red())
            target.sendMessage("Wait for further instructions!".red())
            target.sendMessage("")
            target.sendMessage("")
        }
    }


    @Private
    @Subcommand("set")
    fun onSet(sender: CommandSender, targetRaw: OnlinePlayer, state: Boolean){
        val target = targetRaw.player

        if(state == false){
            target.removeMetadata("frozen", SporeCore.instance)
            Logger.log(sender, Perm.LOG, "unfrozen ${target.name}", true)
            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You are no longer frozen!!".blue())
            target.sendMessage("You may leave the server again.".blue())
            target.sendMessage("")
            target.sendMessage("")
        } else {
            target.setMetadata("frozen", FixedMetadataValue(SporeCore.instance, true))
            Logger.log(sender, Perm.LOG, "froze ${target.name}", true)
            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You have been &bFrozen".red())
            target.sendMessage("Do not leave the server!".red())
            target.sendMessage("Wait for further instructions!".red())
            target.sendMessage("")
            target.sendMessage("")
        }
    }
}
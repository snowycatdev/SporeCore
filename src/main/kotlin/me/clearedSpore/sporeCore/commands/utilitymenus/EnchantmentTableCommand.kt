package me.clearedSpore.sporeCore.commands.utilitymenus

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("enchantmenttable|enchanttable")
@CommandPermission(Perm.ENCHANTMENT)
class EnchantmentTableCommand() : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onEnchantment(player: Player, @Optional targetName: String?){
        if (targetName == null) {
            player.openEnchanting(player.location, true)
            return
        }

        if (!player.hasPermission(Perm.UTILITY_OTHERS)) {
            player.sendMessage("You don't have permission to do this!".red())
            return
        }

        val target = Bukkit.getPlayer(targetName)
        target!!.openEnchanting(player.location, true)
        player.sendMessage("You have opened a Enchantment table for ${target.name}".blue())

    }
}
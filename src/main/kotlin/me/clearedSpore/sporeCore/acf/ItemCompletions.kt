package me.clearedSpore.sporeCore.acf

import co.aikar.commands.PaperCommandManager
import org.bukkit.ChatColor.stripColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag


object ItemCompletions {

    fun PaperCommandManager.registerItemCompletions() {
        commandCompletions.registerCompletion("enchants") { _ ->
            Enchantment.values().map { it.key.key.lowercase() }
        }

        commandCompletions.registerCompletion("attributes") { _ ->
            Attribute.values().map { it.name.lowercase() }
        }


        commandCompletions.registerCompletion("materials") { _ ->
            Material.values().map { it.name.lowercase() }
        }


        commandCompletions.registerCompletion("itemflags") { _ ->
            ItemFlag.values().map { it.name.lowercase() }
        }


        commandCompletions.registerCompletion("bool") { _ ->
            listOf("true", "false")
        }


        commandCompletions.registerCompletion("lorelines") { context ->
            val player = context.sender as? Player ?: return@registerCompletion emptyList()
            val meta = player.inventory.itemInMainHand.itemMeta ?: return@registerCompletion emptyList()
            meta.lore?.mapIndexed { index, _ -> (index + 1).toString() } ?: emptyList()
        }


        commandCompletions.registerCompletion("lorecontent") { context ->
            val player = context.sender as? Player ?: return@registerCompletion emptyList()
            val meta = player.inventory.itemInMainHand.itemMeta ?: return@registerCompletion emptyList()
            val lore = meta.lore ?: return@registerCompletion emptyList()

            val input = context.input
            val parts = input.split(" ")
            val lineStr = parts.getOrNull(3)
            val line = lineStr?.toIntOrNull()?.minus(1) ?: return@registerCompletion emptyList()
            if (line !in lore.indices) return@registerCompletion emptyList()
            val decolored = stripColor(lore[line])
            listOf(decolored)
        }

        commandCompletions.registerCompletion("itemname") { context ->
            val player = context.sender as? Player ?: return@registerCompletion emptyList()
            val meta = player.inventory.itemInMainHand.itemMeta ?: return@registerCompletion emptyList()
            val name = meta.displayName ?: return@registerCompletion emptyList()
            listOf(stripColor(name))
        }
    }
}
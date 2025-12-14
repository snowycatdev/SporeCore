package me.clearedSpore.sporeCore.util

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import me.clearedSpore.sporeCore.SporeCore

class ItemBuilder(private val material: Material, private var amount: Int = 1) {

    private val item: ItemStack = ItemStack(material, amount)
    private val meta: ItemMeta = item.itemMeta ?: throw IllegalStateException("ItemMeta is null for $material")

    fun setName(name: String): ItemBuilder {
        meta.setDisplayName(name)
        return this
    }

    fun setLore(vararg lore: String): ItemBuilder {
        meta.lore = lore.toList()
        return this
    }

    fun setLore(lore: List<String>): ItemBuilder {
        meta.lore = lore
        return this
    }

    fun addLoreLine(line: String): ItemBuilder {
        val currentLore = meta.lore?.toMutableList() ?: mutableListOf()
        currentLore.add(line)
        meta.lore = currentLore
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun addEnchant(enchant: Enchantment, level: Int, ignoreLevelRestriction: Boolean = false): ItemBuilder {
        meta.addEnchant(enchant, level, ignoreLevelRestriction)
        return this
    }

    fun addItemFlag(flag: ItemFlag): ItemBuilder {
        meta.addItemFlags(flag)
        return this
    }

    fun hideEnchantments(): ItemBuilder {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        return this
    }

    fun setGlow(state: Boolean): ItemBuilder {
        meta.setEnchantmentGlintOverride(state)
        return this
    }

    fun hideAttributes(): ItemBuilder {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        return this
    }

    fun hideAll(): ItemBuilder {
        meta.addItemFlags(*ItemFlag.values())
        return this
    }

    fun setSkullOwner(owner: String): ItemBuilder {
        if (meta is SkullMeta) {
            meta.owningPlayer = org.bukkit.Bukkit.getOfflinePlayer(owner)
        }
        return this
    }

    fun addNBTTag(key: String, value: String): ItemBuilder {
        val namespacedKey = NamespacedKey(SporeCore.instance, key)
        meta.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, value)
        return this
    }

    fun build(): ItemStack {
        item.itemMeta = meta
        return item
    }
}

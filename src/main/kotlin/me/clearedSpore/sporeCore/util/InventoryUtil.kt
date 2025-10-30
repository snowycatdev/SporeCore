package me.clearedSpore.sporeCore.util

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

object InventoryUtil {

    fun itemStackListToBase64(items: List<ItemStack?>): String {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use { oos ->
            oos.writeInt(items.size)
            for (item in items) {
                oos.writeObject(item)
            }
        }
        return Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }

    fun itemStackListFromBase64(data: String?): List<ItemStack?> {
        if (data.isNullOrEmpty()) return emptyList()
        val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(data))
        val ois = BukkitObjectInputStream(inputStream)
        val size = ois.readInt()
        val list = mutableListOf<ItemStack?>()
        repeat(size) {
            list.add(ois.readObject() as? ItemStack)
        }
        ois.close()
        return list
    }

    fun itemStackToBase64(item: ItemStack?): String? {
        if (item == null) return null
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use { it.writeObject(item) }
        return Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }

    fun itemStackFromBase64(data: String?): ItemStack? {
        if (data.isNullOrEmpty()) return null
        val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(data))
        BukkitObjectInputStream(inputStream).use {
            return it.readObject() as? ItemStack
        }
    }
}

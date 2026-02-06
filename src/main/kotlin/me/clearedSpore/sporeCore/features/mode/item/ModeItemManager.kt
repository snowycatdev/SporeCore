package me.clearedSpore.sporeCore.features.mode.item

import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.mode.item.impl.BookItem
import me.clearedSpore.sporeCore.features.mode.item.impl.CompassItem
import me.clearedSpore.sporeCore.features.mode.item.impl.FreezeItem
import me.clearedSpore.sporeCore.features.mode.item.impl.HistoryItem
import me.clearedSpore.sporeCore.features.mode.item.impl.PunishSwordItem
import me.clearedSpore.sporeCore.features.mode.item.impl.SpeedItem
import me.clearedSpore.sporeCore.features.mode.item.`object`.ModeItem
import org.bukkit.Bukkit

object ModeItemManager {

    private val items = mutableMapOf<String, ModeItem>()

    fun registerItems() {
        register(FreezeItem())
        register(PunishSwordItem())
        register(SpeedItem())
        register(HistoryItem())
        register(BookItem())
        register(CompassItem())
    }

    fun register(item: ModeItem) {
        val plugin = SporeCore.instance
        items[item.id] = item
        Bukkit.getPluginManager().registerEvents(item, plugin)
    }

    fun getItem(id: String): ModeItem? = items[id]

    fun getAllItems(): Collection<ModeItem> = items.values
}

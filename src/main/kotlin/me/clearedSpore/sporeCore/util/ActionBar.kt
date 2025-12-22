package me.clearedSpore.sporeCore.util

import me.clearedSpore.sporeAPI.util.CC.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ActionBar {

    private data class Entry(
        val text: String,
        val expiresAt: Long
    )

    private val map = ConcurrentHashMap<UUID, MutableMap<String, Entry>>()
    private const val SEPARATOR = " §7| "
    private const val DEFAULT_DURATION = 2000L

    fun put(player: Player, key: String, text: String, durationMillis: Long = DEFAULT_DURATION) {
        val colored = text.translate()
        val expire = if (durationMillis == 0L) Long.MAX_VALUE else System.currentTimeMillis() + durationMillis
        map.computeIfAbsent(player.uniqueId) { mutableMapOf() }
        map[player.uniqueId]!![key] = Entry(colored, expire)
    }

    fun Player.actionBar(key: String, text: String, durationMillis: Long = DEFAULT_DURATION) {
        put(this, key, text, durationMillis)
    }

    fun remove(player: Player, key: String) {
        map[player.uniqueId]?.remove(key)
        if (map[player.uniqueId]?.isEmpty() == true) map.remove(player.uniqueId)
    }

    fun send(player: Player) {
        val playerMap = map[player.uniqueId] ?: return
        val now = System.currentTimeMillis()

        val expired = playerMap.filterValues { it.expiresAt <= now }.keys
        for (key in expired) playerMap.remove(key)

        if (playerMap.isEmpty()) {
            map.remove(player.uniqueId)
            return
        }

        val combined =
            if (playerMap.size == 1)
                playerMap.values.first().text
            else
                playerMap.values.joinToString(SEPARATOR) { it.text }

        player.sendActionBar(combined)
    }


    fun tick() {
        for (player in Bukkit.getOnlinePlayers()) send(player)
    }
}

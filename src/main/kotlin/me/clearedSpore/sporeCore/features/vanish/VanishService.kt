package me.clearedSpore.sporeCore.features.vanish

import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import java.util.*


object VanishService {

    var vanishedPlayers: MutableList<UUID> = mutableListOf()

    fun vanish(uuid: UUID) {
        val userPlayer = Bukkit.getPlayer(uuid) ?: return

        vanishedPlayers.add(uuid)

        for (player in Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Perm.VANISH_SEE)) continue
            player.hidePlayer(SporeCore.instance, userPlayer)
        }
    }


    fun unVanish(uuid: UUID) {
        val userPlayer = Bukkit.getPlayer(uuid) ?: return

        for (player in Bukkit.getOnlinePlayers()) {
            player.showPlayer(SporeCore.instance, userPlayer)
        }

        vanishedPlayers.remove(uuid)
    }

    fun toggle(uuid: UUID) {
        if (isVanished(uuid)) {
            unVanish(uuid)
        } else {
            vanish(uuid)
        }
    }

    fun isVanished(uuid: UUID): Boolean {
        return vanishedPlayers.contains(uuid)
    }

}
package me.clearedSpore.sporeCore.user

import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.eco.EconomyService
import me.clearedSpore.sporeCore.util.Tasks
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class UserListener : Listener {

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player

        val user = UserManager.get(player)

        if (!user.hasJoinedBefore) {
            user.hasJoinedBefore = true
            user.firstJoin = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            val starter = SporeCore.instance.coreConfig.economy.starterBalance
            EconomyService.add(user, starter, "Starter balance")
        }

        if (user.playerName != player.name) user.playerName = player.name

        UserManager.startAutoSave(player)

        if (user.pendingMessages.isNotEmpty()) {
            player.sendMessage("")
            user.pendingMessages.forEach { player.sendMessage(it) }
            player.sendMessage("")
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
            user.pendingMessages.clear()
            user.save()
        }
    }


    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        UserManager.deleteUser(player)
    }
}

package me.clearedSpore.sporeCore.util

import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Task
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.util.ActionBar.actionBar
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object TeleportService {

    val teleportTime = SporeCore.instance.coreConfig.general.teleportTime ?: 5
    private val teleportingPlayers = Collections.newSetFromMap(WeakHashMap<Player, Boolean>())

    fun isTeleporting(player: Player): Boolean {
        return teleportingPlayers.contains(player)
    }


    fun Player.awaitTeleport(location: Location, seconds: Int = teleportTime) {
        val player = this

        if(isTeleporting(player)){
            player.sendMessage("You are already teleporting!".red())
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            return
        }

        teleportingPlayers.add(player)
        if (!player.isOnline) return

        if (player.hasPermission(Perm.TELEPORT_BYPASS)) {
            teleportingPlayers.remove(player)
            player.teleport(location)
            player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            player.actionBar("tp", "Teleported successfully!".blue())
            return
        }

        val key = "teleport-${player.uniqueId}"
        var timeLeft = seconds
        val start = player.location.clone()

        Task.runRepeated(key, Runnable {
            if (!player.isOnline) {
                Task.cancel(key)
                return@Runnable
            }

            val current = player.location
            val moved = abs(current.x - start.x) > 0.3 || abs(current.z - start.z) > 0.3

            if (moved) {
                Task.cancel(key)
                Task.runTask {
                    teleportingPlayers.remove(player)
                    player.actionBar("tp", "Teleportation canceled!".red())
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
                }
                return@Runnable
            }

            if (timeLeft <= 0) {
                Task.runTask {
                    player.teleport(location)
                    teleportingPlayers.remove(player)
                    player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                    player.actionBar("tp",  "Teleported successfully!".blue())
                }
                Task.cancel(key)
                return@Runnable
            }

            Task.runTask {
                player.actionBar("tp",  "Teleporting in ${timeLeft}s...".blue())
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
            }

            timeLeft--
        }, 0L, 1L, TimeUnit.SECONDS)
    }

}

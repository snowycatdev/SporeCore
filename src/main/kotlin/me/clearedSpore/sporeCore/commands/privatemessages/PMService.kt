package me.clearedSpore.sporeCore.commands.privatemessages


import org.bukkit.entity.Player
import java.util.UUID



object PMService {

    private val lastSender = mutableMapOf<UUID, UUID>()

    fun setLastSender(player: Player, target: Player) {
        lastSender[player.uniqueId] = target.uniqueId
        lastSender[target.uniqueId] = player.uniqueId
    }

    fun getLastSender(player: Player): UUID? {
        return lastSender[player.uniqueId]
    }
}

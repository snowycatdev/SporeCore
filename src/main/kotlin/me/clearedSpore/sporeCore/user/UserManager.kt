package me.clearedSpore.sporeCore.user

import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Task
import me.clearedSpore.sporeCore.database.DatabaseManager
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object UserManager {

    private val users = HashMap<UUID, User>()

    fun get(player: Player): User = get(player.uniqueId)

    fun get(uuid: UUID): User {
        getIfLoaded(uuid)?.let { return it }

        return try {
            val user = User(uuid)
            user.playerName = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown"
            users[uuid] = user

            user.mergeFromDatabase().join()

            user
        } catch (ex: Exception) {
            ex.printStackTrace()
            val fallback = User(uuid)
            fallback.playerName = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown"
            users[uuid] = fallback
            fallback
        }
    }

    fun getOffline(uuid: UUID): User {
        getIfLoaded(uuid)?.let { return it }

        val user = User(uuid)
        user.playerName = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown"
        users[uuid] = user

        Task.runAsync { user.mergeFromDatabase() }

        return user
    }

    fun getOffline(player: OfflinePlayer): User = getOffline(player.uniqueId)
    fun getOffline(player: Player): User = getOffline(player.uniqueId)

    fun getIfLoaded(uuid: UUID): User? = users[uuid]
    fun getIfLoaded(player: Player): User? = getIfLoaded(player.uniqueId)

    fun saveUser(uuid: UUID) { getIfLoaded(uuid)?.save() }

    fun saveAllUsers(): CompletableFuture<Void> {
        val loadedUsers = users.values.toList()
        if (loadedUsers.isEmpty()) return CompletableFuture.completedFuture(null)
        return CompletableFuture.allOf(*loadedUsers.map { it.save() }.toTypedArray())
    }

    fun startAutoSave(player: Player) {
        Task.runRepeated(player.uniqueId, {
            saveUser(player.uniqueId)
            Logger.infoDB("Auto-saved User for ${player.name}")
        }, 10, 10, TimeUnit.MINUTES)
    }

    fun stopAutoSave(player: Player) { Task.cancel(player.uniqueId) }

    fun getUserValue(uuid: UUID, key: String) = DatabaseManager.getUserValue(uuid, key)
    fun setUserValue(uuid: UUID, key: String, value: String) = DatabaseManager.setUserValue(uuid, key, value)

    fun getAllPlayerIds(): CompletableFuture<List<UUID>> = DatabaseManager.getAllPlayerUUIDs()

    fun deleteUser(player: Player) {
        users[player.uniqueId]?.save()
        users.remove(player.uniqueId)
        stopAutoSave(player)
    }

    fun deleteUser(uuid: UUID) {
        users[uuid]?.save()
        users.remove(uuid)
    }
}

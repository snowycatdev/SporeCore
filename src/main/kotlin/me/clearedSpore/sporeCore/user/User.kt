package me.clearedSpore.sporeCore.user

import com.google.common.reflect.TypeToken
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.database.gson.Serializer
import me.clearedSpore.sporeCore.features.homes.`object`.Home
import me.clearedSpore.sporeCore.user.settings.Setting
import org.bukkit.Location
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import me.clearedSpore.sporeCore.features.eco.`object`.EcoAction
import me.clearedSpore.sporeCore.features.eco.`object`.EconomyLog
import me.clearedSpore.sporeAPI.util.CC.gray
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeCore.user.UserManager.getIfLoaded

class User(val playerId: UUID) {

    var playerName: String = ""
    var hasJoinedBefore: Boolean = false
    var firstJoin: String? = null
    var pendingMessages: MutableList<String> = mutableListOf()
    var playerSettings: MutableMap<String, Boolean> = mutableMapOf()
    var homes: MutableList<Home> = mutableListOf()
    var balance: Double = 0.0
    var economyLogs: MutableList<EconomyLog> = CopyOnWriteArrayList()

    val player: Player?
        get() = Bukkit.getPlayer(playerId)

    @Volatile
    private var flaggedForSave: Boolean = false

    fun isSettingEnabled(setting: Setting): Boolean =
        playerSettings.getOrDefault(setting.key, setting.defaultValue)

    fun setSetting(setting: Setting, value: Boolean) {
        playerSettings[setting.key] = value
    }

    fun toggleSetting(setting: Setting): Boolean {
        val newValue = !isSettingEnabled(setting)
        setSetting(setting, newValue)
        return newValue
    }

    fun getAllSettings(): Map<Setting, Boolean> =
        Setting.values().associateWith { isSettingEnabled(it) }

    fun logEconomy(action: EcoAction, amount: Double, reason: String = "") {
        economyLogs.add(0, EconomyLog(action, amount, reason, System.currentTimeMillis()))
    }


    fun mergeFromDatabase(): CompletableFuture<Void> {
        val futures = this::class.java.declaredFields.map { field ->
            field.isAccessible = true
            UserManager.getUserValue(playerId, field.name).thenAccept { value ->
                if (value != null) {
                    try {
                        when (field.name) {
                            "playerName", "pendingMessages" -> return@thenAccept
                        }

                        val currentValue = field.get(this)

                        val newValue = when (field.name) {
                            "economyLogs" -> {
                                val type = object : com.google.gson.reflect.TypeToken<List<EconomyLog>>() {}.type
                                val dbLogs: List<EconomyLog> = Serializer.fromJson(value, type)
                                val currentLogs = currentValue as? MutableList<EconomyLog> ?: mutableListOf()
                                (dbLogs + currentLogs)
                                    .distinctBy { it.timestamp }
                                    .sortedByDescending { it.timestamp }
                                    .let { CopyOnWriteArrayList(it) }
                            }
                            else -> {
                                val shouldSet = when (currentValue) {
                                    is String -> currentValue.isEmpty()
                                    is Number -> currentValue.toDouble() == 0.0
                                    is Collection<*> -> currentValue.isEmpty()
                                    is Map<*, *> -> currentValue.isEmpty()
                                    else -> currentValue == null
                                }

                                if (shouldSet) {
                                    when {
                                        field.type == Location::class.java -> Serializer.fromJson(value, Location::class.java)
                                        Map::class.java.isAssignableFrom(field.type) -> Serializer.fromJson(value, Map::class.java)
                                        List::class.java.isAssignableFrom(field.type) -> Serializer.fromJson(value, List::class.java)
                                        else -> Serializer.fromJson(value, field.type)
                                    }
                                } else currentValue
                            }
                        }

                        field.set(this, newValue)
                    } catch (ex: Exception) {
                        Logger.warn("Failed to merge field '${field.name}' for $playerId: ${ex.message}")
                    }
                }
            }
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }


    fun getEconomyLogs(uuid: UUID, page: Int, pageSize: Int = 10): CompletableFuture<List<String>> {
        val loadedUser = getIfLoaded(uuid)

        if (loadedUser != null) {
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, loadedUser.economyLogs.size)
            val logs = if (start >= loadedUser.economyLogs.size) emptyList() else loadedUser.economyLogs.subList(start, end)

            val formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")
            val formatted = logs.map { log ->
                val timestamp = Instant.ofEpochMilli(log.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(formatter)
                    .let { "[$it]".gray() }

                val amountStr = log.action.format(log.amount)
                val reasonStr = if (log.reason.isNotBlank()) " &f${log.reason}".translate() else " No reason found!"
                "$amountStr$reasonStr $timestamp"
            }

            return CompletableFuture.completedFuture(formatted)
        }

        return UserManager.getUserValue(uuid, "economyLogs").thenApply { json ->
            val dbLogs: List<EconomyLog> = if (!json.isNullOrEmpty()) {
                try {
                    val type = object : TypeToken<List<EconomyLog>>() {}.type
                    Serializer.fromJson<List<EconomyLog>>(json, type)
                } catch (e: Exception) {
                    Logger.warn("Failed to parse economy logs for $uuid: ${e.message}")
                    emptyList()
                }
            } else emptyList()

            val combinedLogs = (dbLogs + (loadedUser?.economyLogs ?: emptyList()))
                .distinctBy { it.timestamp }
                .sortedByDescending { it.timestamp }

            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, combinedLogs.size)

            val formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")
            combinedLogs.subList(start, end).map { log ->
                val timestamp = Instant.ofEpochMilli(log.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(formatter)
                    .let { "[$it]".gray() }

                val amountStr = log.action.format(log.amount)
                val reasonStr = if (log.reason.isNotBlank()) " &f${log.reason.translate()}" else ""
                "$amountStr$reasonStr $timestamp"
            }
        }
    }

    fun save(silent: Boolean = false): CompletableFuture<Void> {
        val startTime = System.currentTimeMillis()
        if (!silent) Logger.infoDB("Saving user data for $playerName ($playerId)...")

        val futures = this::class.java.declaredFields.map { field ->
            field.isAccessible = true
            try {
                val value = field.get(this)
                UserManager.setUserValue(playerId, field.name, Serializer.toJson(value))
            } catch (e: Exception) {
                Logger.warn("Failed to save field '${field.name}' for user $playerId: ${e.message}")
                CompletableFuture.completedFuture(null)
            }
        }

        return CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
            if (!silent) Logger.infoDB("Saved user data for $playerName ($playerId). Took ${System.currentTimeMillis() - startTime} ms")
        }
    }

    fun save(fieldName: String, silent: Boolean = false): CompletableFuture<Void> {
        val field = this::class.java.declaredFields.find { it.name == fieldName }
            ?: return CompletableFuture.completedFuture(null)

        field.isAccessible = true
        return try {
            val value = field.get(this)
            UserManager.setUserValue(playerId, field.name, Serializer.toJson(value)).thenRun {
                if (!silent) Logger.infoDB("Saved user field '$fieldName' for $playerName ($playerId)")
            }
        } catch (e: Exception) {
            Logger.warn("Failed to save field '$fieldName' for user $playerId: ${e.message}")
            CompletableFuture.completedFuture(null)
        }
    }
}


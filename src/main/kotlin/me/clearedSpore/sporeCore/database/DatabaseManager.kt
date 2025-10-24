package me.clearedSpore.sporeCore.database

import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Task
import me.clearedSpore.sporeCore.SporeCore
import org.h2.Driver
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.CompletableFuture

object DatabaseManager {

    private var connection: Connection? = null

    fun init(pluginFolder: File) {
        if (!pluginFolder.exists()) pluginFolder.mkdirs()

        val dbFile = File(pluginFolder, "sporecore.db")
        val url = "jdbc:h2:${dbFile.absolutePath.replace("\\", "/")}"

        try {
            val driver = Driver()
            DriverManager.registerDriver(driver)
            connection = DriverManager.getConnection(url, "sa", "")
            createTables()
            Logger.infoDB("Connected to H2 at ${dbFile.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun get(): Database = SporeCore.instance.database

    private fun createTables() {
        val serverSql = """
            CREATE TABLE IF NOT EXISTS server_data (
                id INT AUTO_INCREMENT PRIMARY KEY,
                key_name VARCHAR(255) UNIQUE NOT NULL,
                value_text TEXT NOT NULL
            );
        """.trimIndent()

        val profileSql = """
            CREATE TABLE IF NOT EXISTS player_data (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player_uuid VARCHAR(36) NOT NULL,
                key_name VARCHAR(255) NOT NULL,
                value_text TEXT NOT NULL,
                UNIQUE(player_uuid, key_name)
            );
        """.trimIndent()

        connection?.createStatement()?.execute(serverSql)
        connection?.createStatement()?.execute(profileSql)
    }


    fun setValue(key: String, value: String): CompletableFuture<Void> = Task.runAsync {
        val sql = "MERGE INTO server_data (key_name, value_text) KEY(key_name) VALUES (?, ?);"
        connection?.prepareStatement(sql)?.use { stmt ->
            stmt.setString(1, key)
            stmt.setString(2, value)
            stmt.executeUpdate()
        }
    }

    fun getValue(key: String): CompletableFuture<String?> = Task.supplyAsync {
        val sql = "SELECT value_text FROM server_data WHERE key_name = ?;"
        connection?.prepareStatement(sql)?.use { stmt ->
            stmt.setString(1, key)
            val rs = stmt.executeQuery()
            if (rs.next()) return@supplyAsync rs.getString("value_text")
        }
        null
    }

    fun setUserValue(uuid: UUID, key: String, value: String): CompletableFuture<Void> = Task.runAsync {
        val sql = """
        MERGE INTO player_data (player_uuid, key_name, value_text)
        KEY(player_uuid, key_name) VALUES (?, ?, ?);
    """.trimIndent()
        connection?.prepareStatement(sql)?.use { stmt ->
            stmt.setString(1, uuid.toString())
            stmt.setString(2, key)
            stmt.setString(3, value)
            stmt.executeUpdate()
        }
        null
    }

    fun exists(uuid: UUID): CompletableFuture<Boolean> = Task.supplyAsync {
        val sql = "SELECT 1 FROM player_data WHERE player_uuid = ? LIMIT 1;"
        connection?.prepareStatement(sql)?.use { stmt ->
            stmt.setString(1, uuid.toString())
            val rs = stmt.executeQuery()
            return@supplyAsync rs.next()
        }
        false
    }


    fun getUserValue(uuid: UUID, key: String): CompletableFuture<String?> = Task.supplyAsync {
        val sql = "SELECT value_text FROM player_data WHERE player_uuid = ? AND key_name = ?;"
        connection?.prepareStatement(sql)?.use { stmt ->
            stmt.setString(1, uuid.toString())
            stmt.setString(2, key)
            val rs = stmt.executeQuery()
            if (rs.next()) return@supplyAsync rs.getString("value_text")
        }
        null
    }

    fun removeValue(key: String): CompletableFuture<Void> = Task.runAsync {
        val sql = "DELETE FROM server_data WHERE key_name = ?;"
        connection?.prepareStatement(sql)?.use { stmt ->
            stmt.setString(1, key)
            stmt.executeUpdate()
        }
    }

    fun getAllKeys(): CompletableFuture<List<String>> = Task.supplyAsync {
        val start = System.currentTimeMillis()
        val keys = mutableListOf<String>()
        val sql = "SELECT key_name FROM server_data;"
        connection?.prepareStatement(sql)?.use { stmt ->
            val rs = stmt.executeQuery()
            while (rs.next()) keys.add(rs.getString("key_name"))
        }
        val elapsed = System.currentTimeMillis() - start
        Logger.infoDB("Loaded ${keys.size} keys from server_data in ${elapsed}ms")
        keys
    }

    fun getAllPlayerUUIDs(): CompletableFuture<List<UUID>> = Task.supplyAsync {
        val uuids = mutableListOf<UUID>()
        val sql = "SELECT DISTINCT player_uuid FROM player_data;"
        connection?.prepareStatement(sql)?.use { stmt ->
            val rs = stmt.executeQuery()
            while (rs.next()) {
                val uuidStr = rs.getString("player_uuid")
                try { uuids.add(UUID.fromString(uuidStr)) } catch (_: Exception) {}
            }
        }
        uuids
    }

    fun saveAll(): CompletableFuture<Void> = Task.runAsync {

    }

    fun close() {
        try {
            connection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


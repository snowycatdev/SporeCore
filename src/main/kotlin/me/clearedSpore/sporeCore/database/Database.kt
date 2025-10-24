package me.clearedSpore.sporeCore.database

import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.database.gson.Serializer
import me.clearedSpore.sporeCore.features.warp.`object`.Warp
import org.bukkit.Location
import java.util.concurrent.CompletableFuture
import com.google.gson.reflect.TypeToken

class Database {

    var spawn: String? = null
    var warps: MutableList<Warp> = mutableListOf()

    init {
        loadDatabase()
    }

    fun getSpawn(): Location? =
        spawn?.let { Serializer.fromJson(it, Location::class.java) }

    fun setSpawn(location: Location) {
        spawn = Serializer.toJson(location)
    }

    private fun loadDatabase() {
        Logger.infoDB("Loading Database")
        val startTime = System.currentTimeMillis()

        val futures = this::class.java.declaredFields.map { field ->
            field.isAccessible = true
            DatabaseManager.getValue(field.name).thenAccept { value ->
                try {
                    if (value != null) {
                        val deserialized = when {
                            field.type == Location::class.java -> Serializer.fromJson(value, Location::class.java)

                            List::class.java.isAssignableFrom(field.type) -> {
                                val genericType = (field.genericType as? java.lang.reflect.ParameterizedType)
                                val type = TypeToken.getParameterized(
                                    List::class.java,
                                    genericType?.actualTypeArguments?.get(0) ?: Any::class.java
                                ).type
                                Serializer.fromJson<Any>(value, type)
                            }

                            Map::class.java.isAssignableFrom(field.type) -> {
                                val genericType = (field.genericType as? java.lang.reflect.ParameterizedType)
                                val keyType = genericType?.actualTypeArguments?.get(0) ?: Any::class.java
                                val valueType = genericType?.actualTypeArguments?.get(1) ?: Any::class.java
                                val type = TypeToken.getParameterized(Map::class.java, keyType, valueType).type
                                Serializer.fromJson<Any>(value, type)
                            }

                            else -> Serializer.fromJson(value, field.type)
                        }

                        field.set(this, deserialized)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
            val elapsed = System.currentTimeMillis() - startTime
            Logger.infoDB("Loaded Database. Took $elapsed ms")
        }
    }

    fun saveAll(): CompletableFuture<Void> {
        val startTime = System.currentTimeMillis()
        Logger.infoDB("Saving entire Database...")

        val futures = this::class.java.declaredFields.map { field ->
            field.isAccessible = true
            try {
                val value = field.get(this)
                DatabaseManager.setValue(field.name, Serializer.toJson(value))
            } catch (e: Exception) {
                e.printStackTrace()
                CompletableFuture.completedFuture(null)
            }
        }

        return CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
            val elapsed = System.currentTimeMillis() - startTime
            Logger.infoDB("Saved Database. Took $elapsed ms")
        }
    }


    fun save(fieldName: String): CompletableFuture<Void> {
        val field = this::class.java.declaredFields.find { it.name == fieldName }

        if (field == null) {
            Logger.warn("Attempted to save unknown field '$fieldName'")
            return CompletableFuture.completedFuture(null)
        }

        return try {
            field.isAccessible = true
            val value = field.get(this)
            val json = Serializer.toJson(value)
            Logger.infoDB("Saving '$fieldName' to database...")

            DatabaseManager.setValue(field.name, json).thenRun {
                Logger.infoDB("Saved '$fieldName' successfully.")
            }
        } catch (e: Exception) {
            Logger.warn("Failed to save '$fieldName': ${e.message}")
            e.printStackTrace()
            CompletableFuture.completedFuture(null)
        }
    }
}

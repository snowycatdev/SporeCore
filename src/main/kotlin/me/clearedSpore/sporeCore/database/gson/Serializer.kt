package me.clearedSpore.sporeCore.database.gson

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

object Serializer {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Location::class.java, LocationAdapter)
        .create()

    fun <T> toJson(value: T): String = gson.toJson(value)

    fun <T> fromJson(json: String, type: Type): T = gson.fromJson(json, type)

    fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

    internal inline fun <reified T> fromJson(json: String): T =
        gson.fromJson(json, object : TypeToken<T>() {}.type)


    fun <T> getListType(clazz: Class<T>): Type {
        return object : TypeToken<List<T>>() {}.type
    }

    private object LocationAdapter : JsonSerializer<Location>, JsonDeserializer<Location> {
        override fun serialize(src: Location?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            if (src == null) return JsonNull.INSTANCE
            val obj = JsonObject()
            obj.addProperty("world", src.world?.name ?: "")
            obj.addProperty("x", src.x)
            obj.addProperty("y", src.y)
            obj.addProperty("z", src.z)
            obj.addProperty("yaw", src.yaw)
            obj.addProperty("pitch", src.pitch)
            return obj
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Location {
            val obj = json!!.asJsonObject
            val world = Bukkit.getWorld(obj.get("world").asString)
            val x = obj.get("x").asDouble
            val y = obj.get("y").asDouble
            val z = obj.get("z").asDouble
            val yaw = obj.get("yaw").asFloat
            val pitch = obj.get("pitch").asFloat
            return Location(world, x, y, z, yaw, pitch)
        }
    }
}

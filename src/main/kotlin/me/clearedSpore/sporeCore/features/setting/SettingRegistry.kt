package me.clearedSpore.sporeCore.features.setting

import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.AbstractSetting
import org.bukkit.plugin.java.JavaPlugin
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.createInstance

class SettingRegistry(private val plugin: JavaPlugin) {

    private val settings = mutableMapOf<String, AbstractSetting<*>>()

    fun register(setting: AbstractSetting<*>) {
        settings[setting.key] = setting
    }

    fun get(key: String): AbstractSetting<*>? = settings[key]
    fun all(): Collection<AbstractSetting<*>> = settings.values

    fun loadAllSettings() {
        Logger.info("Loading settings...")

        val packageName = "me.clearedSpore.sporeCore.features.setting.impl"
        val classes = getClassesInPackage(packageName)
        var total = 0

        classes.forEach { clazz ->
            try {
                val kClass = clazz.kotlin
                val instance = kClass.objectInstance ?: kClass.createInstance()

                if (instance is AbstractSetting<*>) {
                    register(instance)
                    total++
                } else {
                    Logger.warn("Class ${clazz.name} is annotated with @Setting but is not an AbstractSetting")
                }
            } catch (ex: Exception) {
                Logger.warn("Failed to instantiate setting ${clazz.name}: ${ex.message}")
            }
        }

        Logger.info("Registered $total settings")
    }

    private fun getClassesInPackage(packageName: String): List<Class<*>> {
        val path = packageName.replace('.', '/')
        val classLoader = plugin.javaClass.classLoader as URLClassLoader
        val classes = mutableListOf<Class<*>>()

        classLoader.urLs.forEach { url ->
            if (url.path.endsWith(".jar")) {
                val jarFile = JarFile(url.path)
                jarFile.entries().asSequence().forEach { entry ->
                    if (entry.name.endsWith(".class") && entry.name.startsWith(path)) {
                        val className = entry.name.removeSuffix(".class").replace('/', '.')
                        try {
                            val clazz = Class.forName(className)
                            if (clazz.getAnnotation(Setting::class.java) != null) {
                                classes.add(clazz)
                            }
                        } catch (_: Exception) {
                        }
                    }
                }
            }
        }

        return classes
    }
}

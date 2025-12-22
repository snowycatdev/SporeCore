package me.clearedSpore.sporeCore.features.setting

import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.annotations.Setting
import me.clearedSpore.sporeCore.features.setting.model.AbstractSetting
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import java.lang.reflect.Modifier

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
                if (!Modifier.isAbstract(clazz.modifiers) && !clazz.isInterface) {
                    val instance = clazz.getDeclaredConstructor().newInstance()
                    if (instance is AbstractSetting<*>) {
                        register(instance)
                        total++
                    } else {
                        Logger.warn("Class ${clazz.name} is annotated with @Setting but is not an AbstractSetting")
                    }
                }
            } catch (ex: Exception) {
                Logger.warn("Failed to instantiate setting ${clazz.name}: ${ex.message}")
            }
        }

        Logger.info("Registered $total settings")
    }

    private fun getClassesInPackage(packageName: String): List<Class<*>> {
        val path = packageName.replace('.', '/')
        val classLoader = plugin.javaClass.classLoader
        val classes = mutableListOf<Class<*>>()

        if (classLoader is URLClassLoader) {
            classLoader.urLs.forEach { url ->
                val file = File(url.toURI())
                if (file.isFile && file.extension == "jar") {
                    JarFile(file).use { jar ->
                        jar.entries().asSequence()
                            .filter { it.name.endsWith(".class") && it.name.startsWith(path) }
                            .forEach { entry ->
                                val className = entry.name.removeSuffix(".class").replace('/', '.')
                                try {
                                    val clazz = Class.forName(className)
                                    if (clazz.getAnnotation(Setting::class.java) != null) {
                                        classes.add(clazz)
                                    }
                                } catch (_: Exception) { }
                            }
                    }
                }
            }
        }

        return classes
    }
}

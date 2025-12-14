package me.clearedSpore.sporeCore.util

import org.bukkit.Location


object Util {

    fun formatNow(): String =
        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    fun String.noTranslate(): String = this


}
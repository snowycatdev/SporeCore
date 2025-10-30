package me.clearedSpore.sporeCore.features.stats

import me.clearedSpore.sporeCore.user.User
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object StatService {
    fun getTotalPlaytime(user: User): Long {
        return user.totalPlaytime
    }

    fun getWeeklyPlaytime(user: User): Long {
        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        return user.playtimeHistory
            .filter { it.first >= weekAgo }
            .sumOf { it.second - it.first }
    }

    fun getFirstJoin(user: User): String {
        val firstJoin = user.firstJoin?.toLongOrNull() ?: return "Never"
        return formatTime(firstJoin)
    }

    fun getLastJoin(user: User): String {
        val lastJoin = user.lastJoin ?: return "Never"
        return formatTime(lastJoin)
    }

    private fun formatTime(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }
}
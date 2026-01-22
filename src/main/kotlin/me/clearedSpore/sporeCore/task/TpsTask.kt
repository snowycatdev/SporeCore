package me.clearedSpore.sporeCore.task

import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.user.UserManager
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.min

object TpsTask : Listener {

    private var running = false
    private val bars = mutableMapOf<Player, BossBar>()

    fun start() {
        if (running) return
        running = true

        object : BukkitRunnable() {
            override fun run() {
                val tps = getCurrentTPS()

                val runtime = Runtime.getRuntime()
                val used = runtime.totalMemory() - runtime.freeMemory()
                val max = runtime.maxMemory()
                val memoryPercent = round1((used.toDouble() / max.toDouble()) * 100.0)

                for (player in Bukkit.getOnlinePlayers()) {
                    val user = UserManager.get(player)
                    if (user != null && user.tpsBar) {
                        val bar = bars.computeIfAbsent(player) {
                            Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID).apply {
                                addPlayer(player)
                                isVisible = true
                            }
                        }

                        val ping = player.ping

                        val tpsColor = colorTps(tps)
                        val memoryColor = colorMemory(memoryPercent)
                        val pingColor = colorPing(ping)

                        val title =
                            "TPS: $tpsColor$tps &r   MEM: $memoryColor$memoryPercent% &r   Ping: $pingColor$ping"
                                .translate()

                        bar.setTitle(title)

                        val score = computeScore(tps, memoryPercent, ping)
                        bar.progress = score
                        bar.color = barColor(score)
                    } else {
                        val bar = bars.remove(player)
                        bar?.removeAll()
                    }
                }
            }
        }.runTaskTimer(SporeCore.instance, 0L, 20L)
    }

    fun stop() {
        if (!running) return
        running = false
        bars.values.forEach { it.isVisible = false }
        bars.clear()
    }

    private fun round1(value: Double): Double {
        return kotlin.math.round(value * 10.0) / 10.0
    }

    private fun getCurrentTPS(): Double {
        return try {
            round1(min(20.0, SporeCore.instance.server.tps[0]))
        } catch (ex: Exception) {
            20.0
        }
    }

    private fun colorTps(tps: Double): String {
        return when {
            tps >= 18.0 -> "&a"
            tps >= 15.0 -> "&6"
            else -> "&c"
        }
    }

    private fun colorMemory(percent: Double): String {
        return when {
            percent < 50.0 -> "&a"
            percent < 70.0 -> "&6"
            else -> "&c"
        }
    }

    private fun colorPing(ping: Int): String {
        return when {
            ping < 100 -> "&a"
            ping < 200 -> "&6"
            else -> "&c"
        }
    }

    private fun computeScore(tps: Double, memoryPercent: Double, ping: Int): Double {
        val tpsScore = 1.0 - (tps / 20.0)
        val memoryScore = min(1.0, memoryPercent / 100.0)
        val pingScore = min(1.0, ping / 300.0)
        return min(1.0, tpsScore * 0.5 + memoryScore * 0.3 + pingScore * 0.2)
    }

    private fun barColor(score: Double): BarColor {
        return when {
            score < 0.33 -> BarColor.GREEN
            score < 0.66 -> BarColor.YELLOW
            else -> BarColor.RED
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        val user = UserManager.get(player)
        if (user != null && user.tpsBar) {
            val bar = bars.computeIfAbsent(player) {
                Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID)
            }
            bar.addPlayer(player)
            bar.isVisible = true
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player
        val bar = bars.remove(player)
        bar?.removeAll()
    }
}

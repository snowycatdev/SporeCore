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
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.math.max
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
                val mspt = (if (tps > 0) 1000.0 / tps else Double.MAX_VALUE).toInt()

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
                        val msptColor = colorMspt(mspt)
                        val pingColor = colorPing(ping)

                        val title = "TPS: $tpsColor$tps &r   MSPT: $msptColor$mspt &r   Ping: $pingColor$ping".translate()
                        bar.setTitle(title)

                        val score = computeScore(tps, mspt, ping)
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
        for (bar in bars.values) {
            bar.isVisible = false
        }
        bars.clear()
    }

    private fun getCurrentTPS(): Double {
        try {
            val server = Bukkit.getServer()
            val minecraftServerClass = server.javaClass.getMethod("getServer").declaringClass
            val serverField: Field = minecraftServerClass.getDeclaredField("recentTps")
            serverField.isAccessible = true
            val recent = serverField.get(server) as DoubleArray
            return min(20.0, recent[0])
        } catch (ex: Exception) {
            return 20.0
        }
    }

    private fun colorTps(tps: Double): String {
        return when {
            tps >= 18.0 -> "&a"
            tps >= 15.0 -> "&6"
            else -> "&c"
        }
    }

    private fun colorMspt(mspt: Int): String {
        return when {
            mspt < 40 -> "&a"
            mspt < 55 -> "&6"
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

    private fun computeScore(tps: Double, mspt: Int, ping: Int): Double {
        val tpsScore = 1.0 - (tps / 20.0)
        val msptScore = min(1.0, mspt / 100.0)
        val pingScore = min(1.0, ping / 300.0)
        return min(1.0, tpsScore * 0.5 + msptScore * 0.3 + pingScore * 0.2)
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

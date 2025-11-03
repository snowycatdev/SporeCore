package me.clearedSpore.sporeCore.features.reboot

import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.TimeUtil
import me.clearedSpore.sporeCore.SporeCore
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import kotlin.math.roundToLong


object RebootService {
    private var rebootTask: BukkitTask? = null
    private var rebootEndTime: Long = 0L
    private var cancelled = false
    private val plugin = SporeCore.instance

    fun startReboot(durationInput: String) {
        val durationMs = TimeUtil.parseDuration(durationInput)
        if (durationMs <= 0) {
            Bukkit.broadcastMessage("Invalid reboot duration: $durationInput".red())
            return
        }

        cancelReboot()

        rebootEndTime = System.currentTimeMillis() + durationMs
        cancelled = false

        sendRebootMessage(durationMs)

        val checkpoints = listOf(0.1, 0.2, 0.5, 0.8)
        val checkpointTimes = checkpoints.map { (durationMs * it).roundToLong() }.toMutableList()

        val countdownSeconds = listOf(5, 4, 3, 2, 1)

        rebootTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            val now = System.currentTimeMillis()
            val remaining = rebootEndTime - now

            if (cancelled) {
                rebootTask?.cancel()
                return@Runnable
            }

            checkpointTimes.forEachIndexed { index, checkpoint ->
                if (remaining <= (durationMs - checkpoint) && checkpoint != -1L) {
                    sendRebootMessage(remaining)
                    checkpointTimes[index] = -1L
                }
            }

            if (remaining <= countdownSeconds.max()!! * 1000) {
                val secondsLeft = (remaining / 1000.0).roundToLong().toInt()
                if (secondsLeft in countdownSeconds) {
                    sendRebootMessage(remaining)
                }
            }

            if (remaining <= 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
                rebootTask?.cancel()
            }
        }, 20L, 20L)
    }

    fun cancelReboot() {
        cancelled = true
        rebootTask?.cancel()
        rebootTask = null
        Bukkit.broadcastMessage("")
        Bukkit.broadcastMessage("Reboot has been cancelled.".red())
        Bukkit.broadcastMessage("")
    }

    private fun sendRebootMessage(timeLeft: Long) {
        val formatted = TimeUtil.formatDuration(timeLeft)
        val title = "§lReboot!!".red()
        val subtitle = "Rebooting in §f$formatted".blue()

        Bukkit.getOnlinePlayers().forEach {
            it.sendTitle(title, subtitle, 10, 20, 10)
        }

        Bukkit.broadcastMessage("")
        Bukkit.broadcastMessage("§lReboot!!".red())
        Bukkit.broadcastMessage("Rebooting in §f$formatted".blue())
        Bukkit.broadcastMessage("")
    }
}
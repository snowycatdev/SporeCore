package me.clearedSpore.sporeCore.hook

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

object WGUtil {

    private fun regionManager(world: World) =
        WorldGuard.getInstance()
            .platform
            .regionContainer
            .get(BukkitAdapter.adapt(world))

    private fun localPlayer(player: Player) =
        WorldGuardPlugin.inst().wrapPlayer(player)

    fun isPvPAllowed(location: Location): Boolean {
        val world = location.world ?: return true
        val query = WorldGuard.getInstance()
            .getPlatform()
            .getRegionContainer()
            .createQuery()

        val state = query.queryState(
            BukkitAdapter.adapt(location),
            null,
            Flags.PVP
        )

        return state != StateFlag.State.DENY
    }

    fun isPvPAllowed(player: Player): Boolean {
        val query = WorldGuard.getInstance()
            .platform
            .regionContainer
            .createQuery()

        val state = query.queryState(
            BukkitAdapter.adapt(player.location),
            localPlayer(player),
            Flags.PVP
        )

        return state != StateFlag.State.DENY
    }

    fun isInRegion(location: Location, regionId: String): Boolean {
        val world = location.world ?: return false
        val manager = regionManager(world) ?: return false
        val region = manager.getRegion(regionId) ?: return false

        return region.contains(
            location.blockX,
            location.blockY,
            location.blockZ
        )
    }

    fun isInRegion(player: Player, regionId: String): Boolean {
        return isInRegion(player.location, regionId)
    }

    fun isInSafeZone(location: Location): Boolean {
        return !isPvPAllowed(location)
    }

    fun isInSafeZone(player: Player): Boolean {
        return !isPvPAllowed(player)
    }
}

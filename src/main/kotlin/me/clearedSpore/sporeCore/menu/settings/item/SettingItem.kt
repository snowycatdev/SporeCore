package me.clearedSpore.sporeCore.menu.settings.item

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import me.clearedSpore.sporeAPI.menu.Item
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.gray
import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.user.User
import me.clearedSpore.sporeCore.user.settings.Setting
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.*


class SettingItem(val user: User, val setting: Setting) : Item() {


    override fun createItem(): ItemStack {
        val enabled = user.isSettingEnabled(setting)
        val item = ItemStack(setting.item)
        val meta = item.itemMeta

        meta.setEnchantmentGlintOverride(enabled)

        setting.skullData?.let { skull ->
            if (item.type == Material.PLAYER_HEAD) {
                val profile = Bukkit.createProfile(UUID.randomUUID(), skull.name)
                profile.setProperty(ProfileProperty("textures", skull.texture, skull.signature))
                (meta as? org.bukkit.inventory.meta.SkullMeta)?.playerProfile = profile
            }
        }

        meta?.setDisplayName(setting.displayName.blue())
        meta?.lore = buildList {
            addAll(setting.lore.map { it.gray() })
            add("")
            add("Status: ${if (enabled) "Enabled".green() else "Disabled".red()}".blue())
        }

        item.itemMeta = meta
        return item
    }

    override fun onClickEvent(clicker: Player, clickType: ClickType) {
        user.toggleSetting(setting)
    }
}
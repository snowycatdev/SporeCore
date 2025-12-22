package me.clearedSpore.sporeCore.menu.settings

import me.clearedSpore.sporeAPI.menu.BasePaginatedMenu
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.menu.settings.item.SettingItem
import me.clearedSpore.sporeCore.user.UserManager
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class SettingsMenu(private val player: Player) : BasePaginatedMenu(SporeCore.instance, false) {

    override fun getMenuName(): String = "Player Settings"
    override fun getRows(): Int = 4

    override fun createItems() {
        val user = UserManager.get(player) ?: run {
            player.userJoinFail()
            return
        }

        val settingRegistry = SporeCore.instance.settingRegistry
        settingRegistry.all().forEach { setting ->
            if (setting.permission != null && !player.hasPermission(setting.permission)) return@forEach
            if (!setting.isEnabledInConfig(SporeCore.instance.coreConfig)) return@forEach

            addItem(SettingItem(user, setting))
        }
    }


    override fun onClose(player: Player) {
        val user = UserManager.get(player) ?: run {
            player.sendMessage("Failed to save user! Some settings may not be saved!".red())
            return
        }

        UserManager.save(user)
    }

    override fun onInventoryClickEvent(clicker: Player, clickType: ClickType, event: InventoryClickEvent) {}
}

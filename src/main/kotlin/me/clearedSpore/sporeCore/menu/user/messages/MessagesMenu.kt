package me.clearedSpore.sporeCore.menu.user.messages

import me.clearedSpore.sporeAPI.menu.BasePaginatedMenu
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.features.investigation.`object`.enum.IGLogType
import me.clearedSpore.sporeCore.features.message.MessageType
import me.clearedSpore.sporeCore.menu.investigation.manage.logs.IGLogsMenu
import me.clearedSpore.sporeCore.menu.investigation.manage.logs.item.IGLogItem
import me.clearedSpore.sporeCore.menu.user.messages.item.MessageItem
import me.clearedSpore.sporeCore.menu.util.EnumFilterItem
import me.clearedSpore.sporeCore.user.User
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent


class MessagesMenu(
    val user: User,
    val viewer: Player,
    val adminMode: Boolean = false,
    val filter: MessageType = MessageType.ALL
) : BasePaginatedMenu(SporeCore.instance, true) {

    override fun getMenuName(): String {
        return "Messages"
    }

    override fun getRows(): Int {
        return 6
    }

    override fun createItems() {
        val messages = user.messages
            .sortedByDescending { it.timestamp }

        if (filter == MessageType.ALL) {
            messages.forEachIndexed { index, message ->
                addItem(MessageItem(user, message, index + 1, viewer, adminMode))
            }
        } else {
            messages.filter { it.type == filter }.forEachIndexed { index, message ->
                addItem(MessageItem(user, message, index + 1, viewer, adminMode))
            }
        }

        val filterItem = EnumFilterItem(
            current = filter,
            values = MessageType.values(),
            title = "Filter"
        ) { player, newStatus ->
            MessagesMenu(user, player, adminMode, newStatus).open(player)
        }

        setGlobalMenuItem(5, 6, filterItem)
    }



    override fun onInventoryClickEvent(
        clicker: Player,
        clickType: ClickType,
        event: InventoryClickEvent
    ) {}

}
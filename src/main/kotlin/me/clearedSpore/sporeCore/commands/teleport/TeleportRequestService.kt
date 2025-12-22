package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.extension.PlayerExtension.uuidStr
import me.clearedSpore.sporeCore.features.logs.LogsService
import me.clearedSpore.sporeCore.features.logs.`object`.LogType
import me.clearedSpore.sporeCore.features.setting.impl.AutoTeleportSetting
import me.clearedSpore.sporeCore.features.setting.impl.ConfirmTpaSetting
import me.clearedSpore.sporeCore.features.setting.impl.TeleportRequestSettings
import me.clearedSpore.sporeCore.menu.util.confirm.TPAConfirmMenu
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.ActionBar.actionBar
import me.clearedSpore.sporeCore.util.TeleportService.awaitTeleport
import org.bukkit.entity.Player

object TeleportRequestService {

    enum class RequestType {
        TPA, TPAHERE
    }

    data class Request(val requester: Player, val target: Player, val type: RequestType)

    private val pendingRequests = mutableMapOf<Player, Request>()

    fun sendRequest(requester: Player, target: Player, type: RequestType) {
        if (requester == target) {
            requester.sendErrorMessage("You cannot send a request to yourself.")
            return
        }

        val requesterUser = UserManager.get(requester)
        val targetUser = UserManager.get(target)

        if (requesterUser == null) {
            Logger.error("Failed to load user for ${requester.name}")
            return
        }

        if (targetUser == null) {
            requester.userJoinFail()
            return
        }

        if (!targetUser.getSettingOrDefault(TeleportRequestSettings())) {
            requester.sendErrorMessage("That player has teleport requests disabled!".red())
            return
        }

        val existingRequest = pendingRequests[target]
        if (existingRequest != null && existingRequest.requester == requester) {
            requester.sendErrorMessage("You have already sent a request to ${target.name}!")
            return
        }

        val request = Request(requester, target, type)
        pendingRequests[target] = request

        val executeRequest = {
            requester.actionBar("tpa", "Teleport request sent to ${target.name}.")
            when (type) {
                RequestType.TPA -> {
                    if (targetUser.getSettingOrDefault(AutoTeleportSetting())) {
                        accept(target)
                        target.actionBar("tpa", "Accepted ${requester.name}'s request (Auto-TP)")
                        pendingRequests.remove(target)
                    } else {
                        target.sendSuccessMessage("${requester.name} wants to teleport to you. Use /tpaaccept or /tpadeny.")
                    }
                }

                RequestType.TPAHERE -> {
                    target.sendSuccessMessage("${requester.name} wants you to teleport to them. Use /tpaaccept or /tpadeny.")
                }
            }
        }

        if (requesterUser.getSettingOrDefault(ConfirmTpaSetting())) {
            TPAConfirmMenu(requester, target, executeRequest as () -> Unit).open(requester)
        } else {
            executeRequest()
        }
    }


    fun accept(target: Player) {
        val request = pendingRequests[target]
        if (request == null) {
            target.sendErrorMessage("You have no pending teleport requests.")
            return
        }

        val targetUser = UserManager.get(target)

        if (targetUser == null) {
            request.requester.userJoinFail()
            return
        }

        val executeTeleport = {
            pendingRequests.remove(target)

            when (request.type) {
                RequestType.TPA -> {
                    request.requester.awaitTeleport(target.location)
                    request.requester.actionBar("tpa", "${target.name} accepted your teleport request.")
                    target.actionBar("tpa", "Accepted ${request.requester.name}'s teleport request.")

                    if (SporeCore.instance.coreConfig.logs.teleports) {
                        LogsService.addLog(
                            request.requester.uuidStr(),
                            "Teleported to ${target.name} (TPA request sent to ${target.name})",
                            LogType.TELEPORT
                        )

                        LogsService.addLog(
                            target.uuidStr(),
                            "${request.requester.name} teleported to you (TPA request sent by ${request.requester.name})",
                            LogType.TELEPORT
                        )
                    }
                }

                RequestType.TPAHERE -> {
                    target.awaitTeleport(request.requester.location)
                    target.actionBar("tpa", "Accepted ${request.requester.name}'s teleport request.")
                    request.requester.actionBar("tpa", "${target.name} accepted your teleport request.")

                    if (SporeCore.instance.coreConfig.logs.teleports) {
                        LogsService.addLog(
                            target.uuidStr(),
                            "Teleported to ${request.requester.name} (TpaHere request sent by ${request.requester.name})",
                            LogType.TELEPORT
                        )

                        LogsService.addLog(
                            request.requester.uuidStr(),
                            "${target.name} teleported to you (TpaHere request sent to ${target.name})",
                            LogType.TELEPORT
                        )
                    }
                }
            }
        }





        if (targetUser.getSettingOrDefault(ConfirmTpaSetting())) {
            TPAConfirmMenu(target, request.requester, executeTeleport).open(target)
        } else {
            executeTeleport()
        }
    }

    fun deny(target: Player) {
        val request = pendingRequests.remove(target)
        if (request == null) {
            target.sendErrorMessage("You have no pending teleport requests.")
            return
        }

        request.requester.sendErrorMessage("Your teleport request to ${request.target.name} was denied.")
        target.sendSuccessMessage("You denied ${request.requester.name}'s teleport request.")
    }
}

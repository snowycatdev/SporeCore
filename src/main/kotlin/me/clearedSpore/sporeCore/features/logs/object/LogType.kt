package me.clearedSpore.sporeCore.features.logs.`object`


enum class LogType(val displayName: String) {
    CHAT("Chat"),
    COMMAND("Command"),
    PRIVATE_MESSAGE("Private Message"),
    TELEPORT("Teleport"),
    FREEZE("Freeze"),
    JOIN_LEAVE("Join Leave")

    ;

}
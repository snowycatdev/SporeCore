package me.clearedSpore.sporeCore

enum class Version(
    val displayName: String,
) {
    DEV("Dev"),
    PUBLIC("Public"),
    PRE_RELEASE("Pre-Release")

    ;

    fun withVersion() : String {
        val version = SporeCore.version
        val plVersion = SporeCore.instance.description.version
        return version.displayName + "-${plVersion}"
    }

}
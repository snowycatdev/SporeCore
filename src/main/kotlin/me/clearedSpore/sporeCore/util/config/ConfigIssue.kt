package me.clearedSpore.sporeCore.util.config

data class ConfigIssue(
    val path: String,
    val message: String,
    val severity: Severity
)

enum class Severity {
    WARNING,
    ERROR
}

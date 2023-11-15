package com.mattmx.ktgui.configuration

data class Configuration(
    var commandConfiguration: CommandConfiguration = CommandConfiguration()
) {
    fun commands(block: CommandConfiguration.() -> Unit) {
        val default = CommandConfiguration()
        block(default)
        this.commandConfiguration = default
    }
}

data class CommandConfiguration(
    var unknownCommandMessage: String = "&cUnknown command.",
    var invalidPermissionsMessage: String = "&cYou do not have permissions to do this.",
    var playerOnlyCommandMessage: String = "&cThis is a player-only command!",
//    var commandUsageGenerator: ((KtCommandBuilder<*>) -> String)? = null
)
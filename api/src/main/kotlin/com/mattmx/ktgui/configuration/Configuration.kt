package com.mattmx.ktgui.configuration

import com.mattmx.ktgui.commands.alpha.KtCommandBuilder
import com.mattmx.ktgui.utils.Invokable

data class Configuration(
    var commandConfiguration: CommandConfiguration = CommandConfiguration()
)

data class CommandConfiguration(
    var unknownCommandMessage: String = "&cUnknown command.",
    var invalidPermissionsMessage: String = "&cYou do not have permissions to do this.",
    var playerOnlyCommandMessage: String = "&cThis is a player-only command!",
    var commandUsageGenerator: KtCommandBuilder<*>.() -> String = { getUsage() }
) : Invokable<CommandConfiguration>
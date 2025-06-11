package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.string
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component

fun createCommand(plugin: Any): RegisteredVelocityCommand {
    val searchQuery by string()
    return command("help") {
        sub(searchQuery) {
            runs<Player> {
                reply(Component.text("You searched for ${searchQuery()}"))
            }
        }

        runs<Player> {
            reply(Component.text("All help commands"))
        }
    }.register(plugin)
}
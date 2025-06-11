package com.mattmx.ktgui.command

import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.CommandSource
import net.kyori.adventure.text.Component

class VelocityCommandContextWrapper<S : CommandSource>(
    context: CommandContext<S>
) : CommandContextWrapper<S>(context) {
    fun reply(msg: Component) = source.sendMessage(msg)
}
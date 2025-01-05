package com.mattmx.ktgui.command

import com.mattmx.ktgui.util.not
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

class CommandContextWrapper<S, H : CommandSender>(
    val context: CommandContext<S>,
    val sender: H
) {
    val source: S
        get() = context.source
    val command: Command<S>
        get() = context.command
    val input: String
        get() = context.input

    inline operator fun <reified T> RequiredArgumentBuilder<S, *>.invoke(): T = context.getArgument(name, T::class.java)

    fun reply(messageString: String) {
        reply(!messageString)
    }

    fun reply(message: Component) {
        sender.sendMessage(message)
    }

}
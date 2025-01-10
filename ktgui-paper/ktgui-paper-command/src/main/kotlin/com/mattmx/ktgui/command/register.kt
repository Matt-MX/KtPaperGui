package com.mattmx.ktgui.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.PaperCommands
import org.bukkit.plugin.Plugin
import java.util.concurrent.atomic.AtomicBoolean

class RegisteredPaperCommand(
    val root: LiteralCommandNode<Any>,
    val enabledStatus: AtomicBoolean,
    val plugin: Plugin
) {

    fun isRegistered(): Boolean {
        return enabledStatus.get()
    }

    fun unregister() {
        CommandManager.unregister(this)
    }

    fun register() {
        CommandManager.register(this)
    }
}

fun unregisterCommand(command: RegisteredPaperCommand, registrar: PaperCommands) {
    (registrar.dispatcher.root as CommandNode<*>).removeCommand(command.root.name)
}

fun LiteralArgumentBuilder<Any>.register(plugin: Plugin): RegisteredPaperCommand {
    val node = build()
    val status = AtomicBoolean(true)

    return RegisteredPaperCommand(node, status, plugin).also(CommandManager::register)
}
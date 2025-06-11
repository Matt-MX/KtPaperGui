package com.mattmx.ktgui.command

import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.CommandSource
import java.util.concurrent.atomic.AtomicBoolean

class RegisteredVelocityCommand(
    val root: LiteralCommandNode<CommandSource>,
    val enabledStatus: AtomicBoolean,
    val plugin: Any
) {
    val meta = CommandManager.getProxyInstance()
        .commandManager
        .metaBuilder(root.name)
        .plugin(plugin)

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
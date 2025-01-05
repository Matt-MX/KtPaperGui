package com.mattmx.ktgui.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin
import java.util.concurrent.atomic.AtomicBoolean

class RegisteredPaperCommand(
    val root: LiteralCommandNode<CommandSourceStack>,
    val eventHandler: LifecycleEventHandler<ReloadableRegistrarEvent<Commands>>,
    val enabledStatus: AtomicBoolean,
    val plugin: Plugin
) {
    fun isRegistered(): Boolean {
        return enabledStatus.get()
    }

    fun unregister() {
        enabledStatus.set(false)
    }

    fun register() {
        enabledStatus.set(true)
    }
}

fun LiteralArgumentBuilder<CommandSourceStack>.register(plugin: Plugin): RegisteredPaperCommand {
    val node = build()
    val status = AtomicBoolean(true)

    // TODO(matt): Currently no way to remove this event callback? Keep track in a manager instead.

    val eventHandler = LifecycleEventHandler<ReloadableRegistrarEvent<Commands>> { event ->
        if (!status.get()) {
            return@LifecycleEventHandler
        }

        event.registrar().register(node)
    }

    plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, eventHandler)

    return RegisteredPaperCommand(node, eventHandler, status, plugin)
}
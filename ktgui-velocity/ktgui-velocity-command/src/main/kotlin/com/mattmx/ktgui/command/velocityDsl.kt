package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentBuilderWrapper
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.plugin.Plugin
import java.util.concurrent.atomic.AtomicBoolean

inline fun <reified S : CommandSource> ArgumentBuilderWrapper.runs(
    crossinline block: VelocityCommandContextWrapper<S>.() -> Unit
) = apply {
    owner.executes { invocation ->

        val castInvocation = invocation as? CommandContext<S>
            ?: return@executes 0

        val wrapper = VelocityCommandContextWrapper<S>(castInvocation)
        block.invoke(wrapper)

        Command.SINGLE_SUCCESS
    }
}

infix fun LiteralArgumentBuilder<*>.register(plugin: Any): RegisteredVelocityCommand {

    if (!plugin.javaClass.isAnnotationPresent(Plugin::class.java)) {
        error("The object's class of argument 'plugin' must have the @Plugin annotation.")
    }

    val node = build() as LiteralCommandNode<CommandSource>
    val status = AtomicBoolean(true)

    return RegisteredVelocityCommand(node, status, plugin).also(CommandManager::register)
}
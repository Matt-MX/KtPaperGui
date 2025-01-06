package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentWrapper
import com.mattmx.ktgui.command.arg.CommandArgumentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender

fun command(name: String, block: (LiteralArgumentBuilder<CommandSourceStack>.() -> Unit)? = null) =
    Commands.literal(name).also { block?.invoke(it) }

fun command(args: CommandArgumentBuilder, block: (ArgumentBuilder<CommandSourceStack, *>.() -> Unit)? = null): LiteralArgumentBuilder<CommandSourceStack> {
    block?.let { args(it) }
    return args.root
}

fun ArgumentBuilder<CommandSourceStack, *>.sub(
    other: String,
    block: (LiteralArgumentBuilder<CommandSourceStack>.() -> Unit)?
) = apply {
    then(command(other, block))
}

fun ArgumentBuilder<CommandSourceStack, *>.sub(
    other: ArgumentWrapper<*>,
    block: (ArgumentBuilder<CommandSourceStack, *>.() -> Unit)?
) = apply {
    val nodeInstance = other.supplier()
    then(nodeInstance.also { block?.invoke(it) })
}

fun ArgumentBuilder<CommandSourceStack, *>.sub(
    other: CommandArgumentBuilder,
    block: (ArgumentBuilder<CommandSourceStack, *>.() -> Unit)?
) = apply {
    then(command(other, block))
}

inline fun <reified S : CommandSender> ArgumentBuilder<CommandSourceStack, *>.runs(
    crossinline block: CommandContextWrapper<CommandSourceStack, S>.() -> Unit
) = apply {
//    val existingExecution = this.command
    val senderClass = S::class.java

    executes { invocation ->
        val wrapper = CommandContextWrapper(invocation, invocation.source.sender)

//        existingExecution?.run(invocation)

        if (senderClass.isInstance(wrapper.sender)) {
            block.invoke(wrapper as CommandContextWrapper<CommandSourceStack, S>)
        }

        Command.SINGLE_SUCCESS
    }
}
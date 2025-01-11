package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentWrapper
import com.mattmx.ktgui.command.arg.CommandArgumentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import java.util.concurrent.CompletableFuture

fun command(name: String, block: (LiteralArgumentBuilder<Any>.() -> Unit)? = null) =
    literal<Any>(name).also { block?.invoke(it) }

fun command(args: CommandArgumentBuilder, block: (ArgumentBuilder<Any, *>.() -> Unit)? = null): LiteralArgumentBuilder<Any> {
    block?.let { args(it) }
    return args.root
}

fun ArgumentBuilder<Any, *>.sub(
    other: String,
    block: (LiteralArgumentBuilder<Any>.() -> Unit)?
) = apply {
    then(command(other, block))
}

fun ArgumentBuilder<Any, *>.sub(
    other: ArgumentWrapper<*>,
    block: (ArgumentBuilder<Any, *>.() -> Unit)?
): ArgumentBuilder<Any, *> {
    val nodeInstance = other.supplier()
    then(nodeInstance.also { block?.invoke(it) })

    return nodeInstance
}

fun ArgumentBuilder<Any, *>.sub(
    other: CommandArgumentBuilder,
    block: (ArgumentBuilder<Any, *>.() -> Unit)?
) = apply {
    then(command(other, block))
}
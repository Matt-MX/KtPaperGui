package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentBuilderWrapper
import com.mattmx.ktgui.command.arg.ArgumentWrapper
import com.mattmx.ktgui.command.arg.CommandArgumentBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import java.util.concurrent.CompletableFuture

fun command(name: String, block: (ArgumentBuilderWrapper.() -> Unit)? = null) =
    literal<Any>(name).also { block?.invoke(ArgumentBuilderWrapper(it)) }

fun command(args: CommandArgumentBuilder, block: (ArgumentBuilderWrapper.() -> Unit)? = null): LiteralArgumentBuilder<Any> {
    block?.let { args(it) }
    return args.root as LiteralArgumentBuilder<Any>
}

fun ArgumentBuilderWrapper.sub(
    other: String,
    block: (ArgumentBuilderWrapper.() -> Unit)?
) = apply {
    owner.then(command(other, block))
}

fun ArgumentBuilderWrapper.sub(
    other: ArgumentWrapper<*>,
    block: (ArgumentBuilder<Any, *>.() -> Unit)?
): ArgumentBuilder<Any, *> {
    val nodeInstance = other.supplier()
    owner.then(nodeInstance.also { block?.invoke(it) })

    return nodeInstance
}

fun ArgumentBuilderWrapper.sub(
    other: CommandArgumentBuilder,
    block: (ArgumentBuilderWrapper.() -> Unit)?
) = apply {
    owner.then(command(other, block))
}
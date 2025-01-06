package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands

class CommandArgumentBuilder(
    val root: LiteralArgumentBuilder<CommandSourceStack>
) {
    var lastArg: ArgumentBuilder<CommandSourceStack, *> = root

    operator fun div(other: ArgumentBuilder<CommandSourceStack, *>): CommandArgumentBuilder {
        lastArg.then(other)
        lastArg = other
        return this
    }

    operator fun div(other: OptionalArgumentWrapper<*>): CommandArgumentBuilder {
        lastArg.then(other.argument)
        return this
    }

    operator fun div(other: String): CommandArgumentBuilder {
        val next = Commands.literal(other)
        lastArg.then(next)
        lastArg = next
        return this
    }

    operator fun invoke(block: ArgumentBuilder<CommandSourceStack, *>.() -> Unit) = apply {
        lastArg.apply(block)
    }
}

operator fun <T : ArgumentBuilder<CommandSourceStack, T>> String.div(arg: ArgumentBuilder<CommandSourceStack, T>): CommandArgumentBuilder {
    return CommandArgumentBuilder(Commands.literal(this)).also { it.div(arg) }
}

operator fun String.div(arg: OptionalArgumentWrapper<*>): CommandArgumentBuilder {
    return CommandArgumentBuilder(Commands.literal(this)).also { it.div(arg) }
}

operator fun String.div(other: String): LiteralArgumentBuilder<CommandSourceStack>? {
    val sub = LiteralArgumentBuilder.literal<CommandSourceStack>(other)
    return Commands.literal(this).then(sub)
}

operator fun <T> RequiredArgumentBuilder<CommandSourceStack, T>.unaryMinus() =
    OptionalArgumentWrapper(this)
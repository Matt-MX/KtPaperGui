package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands

class CommandArgumentBuilder(
    val root: LiteralArgumentBuilder<CommandSourceStack>
) {
    val stack = mutableListOf<Pair<ArgumentBuilder<CommandSourceStack, *>, ArgumentWrapper<*>?>>(
        root to null
    )

    operator fun <T> div(other: ArgumentWrapper<T>): CommandArgumentBuilder {
        val nodeInstance = other.supplier()
        stack.add(nodeInstance to other)
        return this
    }

    operator fun div(other: String): CommandArgumentBuilder {
        val nodeInstance = Commands.literal(other)
        stack.add(nodeInstance to null)
        return this
    }

    operator fun invoke(block: ArgumentBuilder<CommandSourceStack, *>.() -> Unit) = apply {
        // Apply the block to the deepest child that is required
        stack.last { it.second == null || it.second?.isOptional == false }.first.apply(block)
        stack.map { it.first }.reduceRightOrNull { a, b -> a.then(b) as ArgumentBuilder<CommandSourceStack, *> }
    }
}

operator fun <T : Any> String.div(arg: ArgumentWrapper<T>): CommandArgumentBuilder {
    return CommandArgumentBuilder(Commands.literal(this)).also { it.div(arg) }
}

operator fun String.div(other: String): LiteralArgumentBuilder<CommandSourceStack>? {
    val sub = LiteralArgumentBuilder.literal<CommandSourceStack>(other)
    return Commands.literal(this).then(sub)
}

operator fun <T> ArgumentWrapper<T>.unaryMinus() = apply {
    isOptional = true
}
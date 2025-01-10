package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class CommandArgumentBuilder(
    val root: LiteralArgumentBuilder<Any>
) {
    val stack = mutableListOf<Pair<ArgumentBuilder<Any, *>, ArgumentWrapper<*>?>>(
        root to null
    )

    operator fun <T> div(other: ArgumentWrapper<T>): CommandArgumentBuilder {
        val nodeInstance = other.supplier()
        stack.add(nodeInstance to other)
        return this
    }

    operator fun div(other: String): CommandArgumentBuilder {
        val nodeInstance = LiteralArgumentBuilder.literal<Any>(other)
        stack.add(nodeInstance to null)
        return this
    }

    operator fun invoke(block: ArgumentBuilder<Any, *>.() -> Unit) = apply {
        // Apply the block to the deepest child that is required
        stack.last { it.second == null || it.second?.isOptional == false }.first.apply(block)
        stack.map { it.first }.reduceRightOrNull { a, b -> a.then(b) as ArgumentBuilder<Any, *> }
    }
}

operator fun <T : Any> String.div(arg: ArgumentWrapper<T>): CommandArgumentBuilder {
    return CommandArgumentBuilder(LiteralArgumentBuilder.literal(this)).also { it.div(arg) }
}

operator fun String.div(other: String): LiteralArgumentBuilder<Any> {
    val sub = LiteralArgumentBuilder.literal<Any>(other)
    return LiteralArgumentBuilder.literal<Any>(this).then(sub)
}

operator fun <T> ArgumentWrapper<T>.unaryMinus() = apply {
    isOptional = true
}
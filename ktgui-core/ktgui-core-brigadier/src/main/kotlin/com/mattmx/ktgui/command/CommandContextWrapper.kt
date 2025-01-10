package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentWrapper
import com.mattmx.ktgui.command.arg.OptionFlagArgumentType
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext

open class CommandContextWrapper<S>(
    val context: CommandContext<S>
) {
    val source: S
        get() = context.source
    val command: Command<S>
        get() = context.command
    val input: String
        get() = context.input

//    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.resolve() =
//        context.getArgument(name, PlayerSelectorArgumentResolver::class.java)
//            .resolve(source as? CommandSourceStack)
//
//    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.first() = resolve().first()
//    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.forEach(block: (Player) -> Unit) = resolve().forEach(block)
//    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.get() = resolve()
//
//    operator fun ArgumentWrapper<FinePositionResolver>.invoke(): FinePosition =
//        context.getArgument(name, FinePositionResolver::class.java)
//            .resolve(source as? CommandSourceStack)

    operator fun <T> ArgumentWrapper<OptionFlagArgumentType.Result>.get(option: ArgumentWrapper<T>) =
        context.getArgument(name, OptionFlagArgumentType.Result::class.java).map[option] as? T

    inline fun <reified T> ArgumentWrapper<T>.orElse(default: T): T =
        runCatching { context.getArgument(name, T::class.java) }
            .getOrElse { default }

    val ArgumentWrapper<*>.isPresent
        get() = runCatching { context.getArgument(name, Any::class.java) }.getOrNull() != null

    inline operator fun <reified T> ArgumentWrapper<T>.invoke(): T = context.getArgument(name, T::class.java)
}
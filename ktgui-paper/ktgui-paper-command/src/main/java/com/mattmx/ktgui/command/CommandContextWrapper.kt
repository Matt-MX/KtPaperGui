package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentWrapper
import com.mattmx.ktgui.command.arg.OptionHolderArgumentType
import com.mattmx.ktgui.util.not
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import io.papermc.paper.math.FinePosition
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandContextWrapper<S, H : CommandSender>(
    val context: CommandContext<S>,
    val sender: H
) {
    val source: S
        get() = context.source
    val command: Command<S>
        get() = context.command
    val input: String
        get() = context.input

    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.resolve() =
        context.getArgument(name, PlayerSelectorArgumentResolver::class.java)
            .resolve(source as? CommandSourceStack)

    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.first() = resolve().first()
    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.forEach(block: (Player) -> Unit) = resolve().forEach(block)
    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.get() = resolve()

    operator fun ArgumentWrapper<FinePositionResolver>.invoke(): FinePosition =
        context.getArgument(name, FinePositionResolver::class.java)
            .resolve(source as? CommandSourceStack)

    operator fun <T> ArgumentWrapper<OptionHolderArgumentType.Result>.get(option: ArgumentWrapper<T>) =
        context.getArgument(name, OptionHolderArgumentType.Result::class.java).map[option] as? T

    inline fun <reified T> ArgumentWrapper<T>.orElse(default: T): T =
        runCatching { context.getArgument(name, T::class.java) }
            .getOrElse { default }

    inline operator fun <reified T> ArgumentWrapper<T>.invoke(): T = context.getArgument(name, T::class.java)

    fun reply(messageString: String) {
        reply(!messageString)
    }

    fun reply(message: Component) {
        sender.sendMessage(message)
    }

}
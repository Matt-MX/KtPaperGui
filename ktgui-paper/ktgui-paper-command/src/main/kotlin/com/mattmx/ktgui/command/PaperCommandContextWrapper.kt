package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentWrapper
import com.mattmx.ktgui.command.arg.OptionFlagArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import io.papermc.paper.math.FinePosition
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PaperCommandContextWrapper<S : CommandSender>(
    context: CommandContext<CommandSourceStack>
) : CommandContextWrapper<CommandSourceStack>(context) {
    val sender = context.source.sender as S

    operator fun <T> ArgumentWrapper<OptionFlagArgumentType.Result>.get(option: ArgumentWrapper<T>) =
        context.getArgument(name, OptionFlagArgumentType.Result::class.java).map[option] as? T

    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.resolve() =
        context.getArgument(name, PlayerSelectorArgumentResolver::class.java)
            .resolve(source as? CommandSourceStack)

    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.first() = resolve().first()
    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.forEach(block: (Player) -> Unit) = resolve().forEach(block)
    fun ArgumentWrapper<PlayerSelectorArgumentResolver>.get() = resolve()

    operator fun ArgumentWrapper<FinePositionResolver>.invoke(): FinePosition =
        context.getArgument(name, FinePositionResolver::class.java)
            .resolve(source as? CommandSourceStack)

    fun reply(msg: Component) = sender.sendMessage(msg)
}
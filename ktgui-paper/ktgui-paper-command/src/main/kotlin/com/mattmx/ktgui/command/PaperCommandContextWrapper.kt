package com.mattmx.ktgui.command

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender

class PaperCommandContextWrapper<S : CommandSender>(
    context: CommandContext<CommandSourceStack>
) : CommandContextWrapper<CommandSourceStack>(context) {
    val sender = context.source.sender as S
}
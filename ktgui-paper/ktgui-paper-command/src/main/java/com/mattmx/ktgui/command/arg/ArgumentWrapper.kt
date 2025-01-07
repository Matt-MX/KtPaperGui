package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

class ArgumentWrapper<S>(
    val name: String,
    val clazz: Class<S>,
    val argumentType: ArgumentType<S>,
    val supplier: () -> ArgumentBuilder<CommandSourceStack, *>
) {
    var isOptional = false
}
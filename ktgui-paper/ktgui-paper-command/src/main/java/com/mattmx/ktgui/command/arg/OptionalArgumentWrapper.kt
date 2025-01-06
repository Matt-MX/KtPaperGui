package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

class OptionalArgumentWrapper<T>(
    val argument: RequiredArgumentBuilder<CommandSourceStack, T>
)
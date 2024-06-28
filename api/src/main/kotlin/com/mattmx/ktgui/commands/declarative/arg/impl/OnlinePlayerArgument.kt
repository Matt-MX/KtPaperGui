package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import org.bukkit.entity.Player

class OnlinePlayerArgument(
    name: String,
    typeName: String = "player"
) : Argument<Player>(name, typeName, SingleArgumentConsumer())
package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class OnlinePlayerArgument(
    name: String,
    typeName: String = "player"
) : Argument<Player>(name, typeName) {

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): Player? {
        stringValue ?: return null
        return Bukkit.getPlayer(stringValue)
    }
}
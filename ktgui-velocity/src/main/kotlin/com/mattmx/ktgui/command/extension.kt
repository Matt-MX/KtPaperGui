package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentBuilderWrapper
import com.mattmx.ktgui.screen.GuiScreen
import com.velocitypowered.api.proxy.Player

inline fun ArgumentBuilderWrapper.opensGui(
    crossinline block: VelocityCommandContextWrapper<Player>.() -> GuiScreen<Any, *>
) = runs<Player> {
    block(this).open(source)
}
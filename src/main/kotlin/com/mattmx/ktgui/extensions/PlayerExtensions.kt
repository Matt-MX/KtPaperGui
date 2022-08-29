package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.entity.Player

fun Player.getOpenGui() : IGuiScreen? {
    return GuiManager.players[this.uniqueId]
}

fun Player.setOpenGui(gui: IGuiScreen) {
    GuiManager.players[this.uniqueId] = gui
}
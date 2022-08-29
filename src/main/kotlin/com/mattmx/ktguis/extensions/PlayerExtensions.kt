package com.mattmx.ktguis.extensions

import com.mattmx.ktguis.GuiManager
import com.mattmx.ktguis.components.IGuiScreen
import org.bukkit.entity.Player

fun Player.getOpenGui() : IGuiScreen? {
    return GuiManager.players[this.uniqueId]
}

fun Player.setOpenGui(gui: IGuiScreen) {
    GuiManager.players[this.uniqueId] = gui
}
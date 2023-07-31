package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.getOpenGui() : IGuiScreen? {
    return com.mattmx.ktgui.GuiManager.players[this.uniqueId]
}

fun Player.setOpenGui(gui: IGuiScreen) {
    com.mattmx.ktgui.GuiManager.players[this.uniqueId] = gui
}

fun Player.removeScoreboard() {
    this.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
}
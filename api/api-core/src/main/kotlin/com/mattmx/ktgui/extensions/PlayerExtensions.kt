package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.getOpenGui(): IGuiScreen? {
    return GuiManager.getPlayer(this)
}

fun Player.setOpenGui(gui: IGuiScreen) {
    GuiManager.setOpenGui(this, gui)
}

fun Player.removeScoreboard() {
    this.scoreboard = Bukkit.getScoreboardManager().newScoreboard
}
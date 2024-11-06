package com.mattmx.ktgui

import com.mattmx.ktgui.components.button.named
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.screen.slot.Slots
import com.mattmx.ktgui.components.title
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player

fun createMenu(player: Player): GuiScreen {
    return gui(3) {
        title { !"Balls" }

        button(Material.RED_STAINED_GLASS) {
            named { !"Test" }
        } slot slots.of(3, 3)

    } andOpen player
}
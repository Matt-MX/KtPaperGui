package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.screen.GuiScreen
import net.kyori.adventure.text.Component

fun GuiScreen.title(builder: () -> Component) {
    title = builder()

    onRefresh {
        title = builder()
    }
}
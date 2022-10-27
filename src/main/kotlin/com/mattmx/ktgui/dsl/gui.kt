package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.components.screen.GuiScreen

fun gui(gui: GuiScreen.() -> Unit) : GuiScreen {
    val g = GuiScreen()
    gui.invoke(g)
    return g
}
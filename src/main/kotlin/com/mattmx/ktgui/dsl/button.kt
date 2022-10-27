package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.IGuiScreen

inline fun button(parent: IGuiScreen? = null, button: GuiButton.() -> Unit) : GuiButton {
    val b = GuiButton()
    button.invoke(b)
    parent?.let { b childOf parent }
    return b
}